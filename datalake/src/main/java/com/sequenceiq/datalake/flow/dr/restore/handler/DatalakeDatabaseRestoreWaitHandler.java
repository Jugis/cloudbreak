package com.sequenceiq.datalake.flow.dr.restore.handler;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.dyngr.exception.PollerException;
import com.dyngr.exception.PollerStoppedException;
import com.dyngr.exception.UserBreakException;
import com.sequenceiq.cloudbreak.common.event.Selectable;
import com.sequenceiq.datalake.flow.dr.restore.event.DatalakeDatabaseRestoreFailedEvent;
import com.sequenceiq.datalake.flow.dr.restore.event.DatalakeDatabaseRestoreSuccessEvent;
import com.sequenceiq.datalake.flow.dr.restore.event.DatalakeDatabaseRestoreWaitRequest;
import com.sequenceiq.datalake.service.sdx.PollingConfig;
import com.sequenceiq.datalake.service.sdx.dr.SdxDatabaseDrService;
import com.sequenceiq.flow.event.EventSelectorUtil;
import com.sequenceiq.flow.reactor.api.handler.ExceptionCatcherEventHandler;

import reactor.bus.Event;

@Component
public class DatalakeDatabaseRestoreWaitHandler extends ExceptionCatcherEventHandler<DatalakeDatabaseRestoreWaitRequest> {

    private static final Logger LOGGER = LoggerFactory.getLogger(DatalakeDatabaseRestoreWaitHandler.class);

    @Value("${sdx.stack.restore.status.sleeptime_sec:20}")
    private int sleepTimeInSec;

    @Value("${sdx.stack.restore.status.duration_min:90}")
    private int durationInMinutes;

    @Inject
    private SdxDatabaseDrService sdxDatabaseDrService;

    @Override
    public String selector() {
        return EventSelectorUtil.selector(DatalakeDatabaseRestoreWaitRequest.class);
    }

    @Override
    protected Selectable defaultFailureEvent(Long resourceId, Exception e, Event<DatalakeDatabaseRestoreWaitRequest> event) {
        return new DatalakeDatabaseRestoreFailedEvent(resourceId, null, e);
    }

    @Override
    protected Selectable doAccept(HandlerEvent event) {
        DatalakeDatabaseRestoreWaitRequest request = event.getData();
        Long sdxId = request.getResourceId();
        String userId = request.getUserId();
        Selectable response;
        try {
            LOGGER.info("Start polling datalake database restore for id: {}", sdxId);
            PollingConfig pollingConfig = new PollingConfig(sleepTimeInSec, TimeUnit.SECONDS, durationInMinutes, TimeUnit.MINUTES);
            sdxDatabaseDrService.waitCloudbreakFlow(sdxId, pollingConfig, "Database restore");
            response = new DatalakeDatabaseRestoreSuccessEvent(sdxId, userId, request.getOperationId());
        } catch (UserBreakException userBreakException) {
            LOGGER.info("Database restore polling exited before timeout. Cause: ", userBreakException);
            response = new DatalakeDatabaseRestoreFailedEvent(sdxId, userId, userBreakException);
        } catch (PollerStoppedException pollerStoppedException) {
            LOGGER.info("Database restore poller stopped for cluster: {}", sdxId);
            response = new DatalakeDatabaseRestoreFailedEvent(sdxId, userId,
                    new PollerStoppedException("Database restore timed out after " + durationInMinutes + " minutes"));
        } catch (PollerException exception) {
            LOGGER.info("Database restore polling failed for cluster: {}", sdxId);
            response = new DatalakeDatabaseRestoreFailedEvent(sdxId, userId, exception);
        }
        return response;
    }
}
