/**
 * NOTE: This class is auto generated by the swagger code generator program (2.4.16).
 * https://github.com/swagger-api/swagger-codegen
 * Do not edit the class manually.
 */
package com.sequenceiq.mock.swagger.v40.api;

import com.sequenceiq.mock.swagger.model.ApiImpalaCancelResponse;
import com.sequenceiq.mock.swagger.model.ApiImpalaQueryAttributeList;
import com.sequenceiq.mock.swagger.model.ApiImpalaQueryDetailsResponse;
import com.sequenceiq.mock.swagger.model.ApiImpalaQueryResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.*;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2020-10-26T08:00:53.907+01:00")

@Api(value = "ImpalaQueriesResource", description = "the ImpalaQueriesResource API")
@RequestMapping(value = "/api/v40")
public interface ImpalaQueriesResourceApi {

    Logger log = LoggerFactory.getLogger(ImpalaQueriesResourceApi.class);

    default Optional<ObjectMapper> getObjectMapper() {
        return Optional.empty();
    }

    default Optional<HttpServletRequest> getRequest() {
        return Optional.empty();
    }

    default Optional<String> getAcceptHeader() {
        return getRequest().map(r -> r.getHeader("Accept"));
    }

    @ApiOperation(value = "Cancels an Impala Query.", nickname = "cancelImpalaQuery", notes = "Cancels an Impala Query. <p> Available since API v4.", response = ApiImpalaCancelResponse.class, authorizations = {
        @Authorization(value = "basic")
    }, tags={ "ImpalaQueriesResource", })
    @ApiResponses(value = { 
        @ApiResponse(code = 201, message = "Success", response = ApiImpalaCancelResponse.class) })
    @RequestMapping(value = "/clusters/{clusterName}/services/{serviceName}/impalaQueries/{queryId}/cancel",
        produces = { "application/json" }, 
        method = RequestMethod.POST)
    default ResponseEntity<ApiImpalaCancelResponse> cancelImpalaQuery(@ApiParam(value = "",required=true) @PathVariable("clusterName") String clusterName,@ApiParam(value = "The queryId to cancel",required=true) @PathVariable("queryId") String queryId,@ApiParam(value = "The name of the service",required=true) @PathVariable("serviceName") String serviceName) {
        if(getObjectMapper().isPresent() && getAcceptHeader().isPresent()) {
            if (getAcceptHeader().get().contains("application/json")) {
                try {
                    return new ResponseEntity<>(getObjectMapper().get().readValue("{  \"warning\" : \"...\"}", ApiImpalaCancelResponse.class), HttpStatus.NOT_IMPLEMENTED);
                } catch (IOException e) {
                    log.error("Couldn't serialize response for content type application/json", e);
                    return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
                }
            }
        } else {
            log.warn("ObjectMapper or HttpServletRequest not configured in default ImpalaQueriesResourceApi interface so no example is generated");
        }
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }


    @ApiOperation(value = "Returns a list of queries that satisfy the filter.", nickname = "getImpalaQueries", notes = "Returns a list of queries that satisfy the filter <p> Available since API v4.", response = ApiImpalaQueryResponse.class, authorizations = {
        @Authorization(value = "basic")
    }, tags={ "ImpalaQueriesResource", })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "Success", response = ApiImpalaQueryResponse.class) })
    @RequestMapping(value = "/clusters/{clusterName}/services/{serviceName}/impalaQueries",
        produces = { "application/json" }, 
        method = RequestMethod.GET)
    default ResponseEntity<ApiImpalaQueryResponse> getImpalaQueries(@ApiParam(value = "",required=true) @PathVariable("clusterName") String clusterName,@ApiParam(value = "The name of the service",required=true) @PathVariable("serviceName") String serviceName,@ApiParam(value = "A filter to apply to the queries. A basic filter tests the value of an attribute and looks something like 'rowsFetched = 1' or 'user = root'. Multiple basic filters can be combined into a complex expression using standard and / or boolean logic and parenthesis. An example of a complex filter is: 'query_duration > 5s and (user = root or user = myUserName)'. An example of expected full query string in requested URL is: '?filter=(query_duration > 5s and (user = root or user = myUserName))'.", defaultValue = "") @Valid @RequestParam(value = "filter", required = false, defaultValue="") String filter,@ApiParam(value = "Start of the period to query in ISO 8601 format (defaults to 5 minutes before the 'to' time).") @Valid @RequestParam(value = "from", required = false) String from,@ApiParam(value = "The maximum number of queries to return. Queries will be returned in the following order: <ul> <li> All executing queries, ordered from longest to shortest running </li> <li> All completed queries order by end time descending. </li> </ul>", defaultValue = "100") @Valid @RequestParam(value = "limit", required = false, defaultValue="100") Integer limit,@ApiParam(value = "The offset to start returning queries from. This is useful for paging through lists of queries. Note that this has non-deterministic behavior if executing queries are included in the response because they can disappear from the list while paging. To exclude executing queries from the response and a 'executing = false' clause to your filter.", defaultValue = "0") @Valid @RequestParam(value = "offset", required = false, defaultValue="0") Integer offset,@ApiParam(value = "End of the period to query in ISO 8601 format (defaults to current time).", defaultValue = "now") @Valid @RequestParam(value = "to", required = false, defaultValue="now") String to) {
        if(getObjectMapper().isPresent() && getAcceptHeader().isPresent()) {
            if (getAcceptHeader().get().contains("application/json")) {
                try {
                    return new ResponseEntity<>(getObjectMapper().get().readValue("{  \"queries\" : [ {    \"queryId\" : \"...\",    \"statement\" : \"...\",    \"queryType\" : \"...\",    \"queryState\" : \"...\",    \"startTime\" : \"...\",    \"endTime\" : \"...\",    \"rowsProduced\" : 12345,    \"attributes\" : {      \"property1\" : \"...\",      \"property2\" : \"...\"    },    \"user\" : \"...\",    \"coordinator\" : {      \"hostId\" : \"...\",      \"hostname\" : \"...\"    },    \"detailsAvailable\" : true,    \"database\" : \"...\",    \"durationMillis\" : 12345  }, {    \"queryId\" : \"...\",    \"statement\" : \"...\",    \"queryType\" : \"...\",    \"queryState\" : \"...\",    \"startTime\" : \"...\",    \"endTime\" : \"...\",    \"rowsProduced\" : 12345,    \"attributes\" : {      \"property1\" : \"...\",      \"property2\" : \"...\"    },    \"user\" : \"...\",    \"coordinator\" : {      \"hostId\" : \"...\",      \"hostname\" : \"...\"    },    \"detailsAvailable\" : true,    \"database\" : \"...\",    \"durationMillis\" : 12345  } ],  \"warnings\" : [ \"...\", \"...\" ]}", ApiImpalaQueryResponse.class), HttpStatus.NOT_IMPLEMENTED);
                } catch (IOException e) {
                    log.error("Couldn't serialize response for content type application/json", e);
                    return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
                }
            }
        } else {
            log.warn("ObjectMapper or HttpServletRequest not configured in default ImpalaQueriesResourceApi interface so no example is generated");
        }
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }


    @ApiOperation(value = "Returns the list of all attributes that the Service Monitor can associate with Impala queries.", nickname = "getImpalaQueryAttributes", notes = "Returns the list of all attributes that the Service Monitor can associate with Impala queries. <p> Examples of attributes include the user who issued the query and the number of HDFS bytes read by the query. <p> These attributes can be used to search for specific Impala queries through the getImpalaQueries API. For example the 'user' attribute could be used in the search 'user = root'. If the attribute is numeric it can also be used as a metric in a tsquery (ie, 'select hdfs_bytes_read from IMPALA_QUERIES'). <p> Note that this response is identical for all Impala services. <p> Available since API v6.", response = ApiImpalaQueryAttributeList.class, authorizations = {
        @Authorization(value = "basic")
    }, tags={ "ImpalaQueriesResource", })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "Success", response = ApiImpalaQueryAttributeList.class) })
    @RequestMapping(value = "/clusters/{clusterName}/services/{serviceName}/impalaQueries/attributes",
        produces = { "application/json" }, 
        method = RequestMethod.GET)
    default ResponseEntity<ApiImpalaQueryAttributeList> getImpalaQueryAttributes(@ApiParam(value = "",required=true) @PathVariable("clusterName") String clusterName,@ApiParam(value = "",required=true) @PathVariable("serviceName") String serviceName) {
        if(getObjectMapper().isPresent() && getAcceptHeader().isPresent()) {
            if (getAcceptHeader().get().contains("application/json")) {
                try {
                    return new ResponseEntity<>(getObjectMapper().get().readValue("{  \"items\" : [ {    \"name\" : \"...\",    \"type\" : \"...\",    \"displayName\" : \"...\",    \"supportsHistograms\" : true,    \"description\" : \"...\"  }, {    \"name\" : \"...\",    \"type\" : \"...\",    \"displayName\" : \"...\",    \"supportsHistograms\" : true,    \"description\" : \"...\"  } ]}", ApiImpalaQueryAttributeList.class), HttpStatus.NOT_IMPLEMENTED);
                } catch (IOException e) {
                    log.error("Couldn't serialize response for content type application/json", e);
                    return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
                }
            }
        } else {
            log.warn("ObjectMapper or HttpServletRequest not configured in default ImpalaQueriesResourceApi interface so no example is generated");
        }
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }


    @ApiOperation(value = "Returns details about the query.", nickname = "getQueryDetails", notes = "Returns details about the query. Not all queries have details, check the detailsAvailable field from the getQueries response. <p> Available since API v4.", response = ApiImpalaQueryDetailsResponse.class, authorizations = {
        @Authorization(value = "basic")
    }, tags={ "ImpalaQueriesResource", })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "Success", response = ApiImpalaQueryDetailsResponse.class) })
    @RequestMapping(value = "/clusters/{clusterName}/services/{serviceName}/impalaQueries/{queryId}",
        produces = { "application/json" }, 
        method = RequestMethod.GET)
    default ResponseEntity<ApiImpalaQueryDetailsResponse> getQueryDetails(@ApiParam(value = "",required=true) @PathVariable("clusterName") String clusterName,@ApiParam(value = "The queryId to get information about",required=true) @PathVariable("queryId") String queryId,@ApiParam(value = "",required=true) @PathVariable("serviceName") String serviceName,@ApiParam(value = "There are two valid format parameters: <ul> <li> 'text': this is a text based, human readable representation of the Impala runtime profile. </li> <li> 'thrift_encoded': this a compact-thrift, base64 encoded representation of the Impala RuntimeProfile.thrift object. See the Impala documentation for more details. </li> </ul>", defaultValue = "text") @Valid @RequestParam(value = "format", required = false, defaultValue="text") String format) {
        if(getObjectMapper().isPresent() && getAcceptHeader().isPresent()) {
            if (getAcceptHeader().get().contains("application/json")) {
                try {
                    return new ResponseEntity<>(getObjectMapper().get().readValue("{  \"details\" : \"...\"}", ApiImpalaQueryDetailsResponse.class), HttpStatus.NOT_IMPLEMENTED);
                } catch (IOException e) {
                    log.error("Couldn't serialize response for content type application/json", e);
                    return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
                }
            }
        } else {
            log.warn("ObjectMapper or HttpServletRequest not configured in default ImpalaQueriesResourceApi interface so no example is generated");
        }
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

}
