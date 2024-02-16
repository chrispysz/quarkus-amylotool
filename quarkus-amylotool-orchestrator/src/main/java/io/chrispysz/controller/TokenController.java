package io.chrispysz.controller;

import io.chrispysz.entity.Token;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.jboss.logging.Logger;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("/api/token")
@Tag(name = "tokens")
public class TokenController {

    Logger logger;

    public TokenController(Logger logger) {
        this.logger = logger;
    }

    @Operation(summary = "Returns token data")
    @GET
    @Path("/{value}")
    @APIResponse(responseCode = "200", content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = Token.class)))
    @APIResponse(responseCode = "204", description = "Token not found for a given value")
    public Response getTokenData(@PathParam("value") String value) {
        Token token = Token.find("value", value).firstResult();
        if (token != null) {
            return Response.ok(token).build();
        } else {
            logger.debugf("No Token found with value %d", value);
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }
}
