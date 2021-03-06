package com.sequenceiq.authorization.service;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import com.sequenceiq.authorization.annotation.CustomPermissionCheck;
import com.sequenceiq.authorization.annotation.DisableCheckPermissions;
import com.sequenceiq.authorization.annotation.FilterListBasedOnPermissions;
import com.sequenceiq.authorization.annotation.InternalOnly;
import com.sequenceiq.authorization.service.list.ListPermissionChecker;
import com.sequenceiq.authorization.util.AuthorizationAnnotationUtils;
import com.sequenceiq.cloudbreak.auth.InternalCrnBuilder;
import com.sequenceiq.cloudbreak.auth.ReflectionUtil;
import com.sequenceiq.cloudbreak.auth.ThreadBasedUserCrnProvider;
import com.sequenceiq.cloudbreak.auth.altus.Crn;
import com.sequenceiq.cloudbreak.auth.security.CrnUserDetailsService;
import com.sequenceiq.cloudbreak.auth.security.internal.InitiatorUserCrn;
import com.sequenceiq.cloudbreak.auth.security.internal.InternalUserModifier;

@Service
public class PermissionCheckService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PermissionCheckService.class);

    @Inject
    private CommonPermissionCheckingUtils commonPermissionCheckingUtils;

    @Inject
    private List<PermissionChecker<? extends Annotation>> permissionCheckers;

    @Inject
    private ListPermissionChecker listPermissionChecker;

    @Inject
    private InternalUserModifier internalUserModifier;

    @Inject
    private CrnUserDetailsService crnUserDetailsService;

    @Inject
    private ReflectionUtil reflectionUtil;

    private final Map<Class<? extends Annotation>, PermissionChecker<? extends Annotation>> permissionCheckerMap = new HashMap<>();

    @PostConstruct
    public void populatePermissionCheckMap() {
        permissionCheckers.forEach(permissionChecker -> permissionCheckerMap.put(permissionChecker.supportedAnnotation(), permissionChecker));
    }

    public Object hasPermission(ProceedingJoinPoint proceedingJoinPoint) {
        long startTime = System.currentTimeMillis();
        MethodSignature methodSignature = (MethodSignature) proceedingJoinPoint.getSignature();
        LOGGER.debug("Permission check started at {} (method: {})", startTime,
                methodSignature.getMethod().getDeclaringClass().getSimpleName() + "#" + methodSignature.getMethod().getName());
        Optional<Object> initiatorUserCrn = reflectionUtil.getParameter(proceedingJoinPoint, methodSignature, InitiatorUserCrn.class);
        if (InternalCrnBuilder.isInternalCrn(ThreadBasedUserCrnProvider.getUserCrn())
                && initiatorUserCrn.isPresent()
                && initiatorUserCrn.get() instanceof String && Crn.isCrn((String) initiatorUserCrn.get())) {
            String newUserCrn = (String) initiatorUserCrn.get();
            internalUserModifier.persistModifiedInternalUser(crnUserDetailsService.loadUserByUsername(newUserCrn));
            return ThreadBasedUserCrnProvider.doAs(newUserCrn, () -> commonPermissionCheckingUtils.proceed(proceedingJoinPoint, methodSignature, startTime));
        }

        if (commonPermissionCheckingUtils.isAuthorizationDisabled(proceedingJoinPoint) ||
                InternalCrnBuilder.isInternalCrn(ThreadBasedUserCrnProvider.getUserCrn())) {
            return commonPermissionCheckingUtils.proceed(proceedingJoinPoint, methodSignature, startTime);
        }

        checkPrerequisitesOnClass(proceedingJoinPoint, methodSignature);

        return checkPermission(proceedingJoinPoint, methodSignature, startTime);
    }

    protected Object checkPermission(ProceedingJoinPoint proceedingJoinPoint, MethodSignature methodSignature, long startTime) {
        String userCrn = ThreadBasedUserCrnProvider.getUserCrn();

        List<? extends Annotation> annotations = AuthorizationAnnotationUtils.getPossibleMethodAnnotations().stream()
                .map(c -> methodSignature.getMethod().getAnnotation(c))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        checkPrerequisitesOnMethod(methodSignature, annotations);

        if (annotations.stream().anyMatch(annotation -> annotation instanceof DisableCheckPermissions)) {
            return commonPermissionCheckingUtils.proceed(proceedingJoinPoint, methodSignature, startTime);
        } else if (annotations.stream().anyMatch(annotation -> annotation instanceof CustomPermissionCheck)) {
            return commonPermissionCheckingUtils.proceed(proceedingJoinPoint, methodSignature, startTime);
        } else if (annotations.stream().anyMatch(annotation -> annotation instanceof FilterListBasedOnPermissions)) {
            FilterListBasedOnPermissions listFilterAnnotation = (FilterListBasedOnPermissions) annotations.stream()
                    .filter(annotation -> annotation instanceof FilterListBasedOnPermissions).findFirst().get();
            return listPermissionChecker.checkPermissions(listFilterAnnotation, userCrn, proceedingJoinPoint, methodSignature, startTime);
        }

        annotations.stream().forEach(annotation -> {
            PermissionChecker<? extends Annotation> permissionChecker = permissionCheckerMap.get(annotation.annotationType());
            permissionChecker.checkPermissions(annotation, userCrn, proceedingJoinPoint, methodSignature, startTime);
        });
        return commonPermissionCheckingUtils.proceed(proceedingJoinPoint, methodSignature, startTime);
    }

    private void checkPrerequisitesOnClass(ProceedingJoinPoint proceedingJoinPoint, MethodSignature methodSignature) {
        if (commonPermissionCheckingUtils.isInternalOnly(proceedingJoinPoint) &&
                !InternalCrnBuilder.isInternalCrn(ThreadBasedUserCrnProvider.getUserCrn())) {
            getAccessDeniedAndLogInternalActorRestriction(methodSignature);
        }
    }

    private void checkPrerequisitesOnMethod(MethodSignature methodSignature, List<? extends Annotation> annotations) {
        if (annotations.stream().anyMatch(annotation -> annotation instanceof InternalOnly) &&
            !InternalCrnBuilder.isInternalCrn(ThreadBasedUserCrnProvider.getUserCrn())) {
            throw getAccessDeniedAndLogInternalActorRestriction(methodSignature);
        }
    }

    private AccessDeniedException getAccessDeniedAndLogInternalActorRestriction(MethodSignature methodSignature) {
        LOGGER.error("Method {} should be called by internal actor only.",
                methodSignature.getMethod().getDeclaringClass().getSimpleName()  + "#" + methodSignature.getMethod().getName());
        return new AccessDeniedException("You have no access to this resource.");
    }
}
