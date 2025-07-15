package com.motivation.ietec_cdc;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author EgorBusuioc
 * 08.05.2025
 */
@Tag(name = "Test", description = "Test API service")
@RestController
public class TestController {

    @Operation(
            summary = "Test your JWT token",
            description = "You can test your JWT token here",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Token is valid",
                            content = @Content(
                                    schema = @Schema(implementation = String.class),
                                    examples = @ExampleObject(value = "Token is valid"))),
                    @ApiResponse(responseCode = "401", description = "Invalid or expired token",
                            content = @Content(
                                    schema = @Schema(implementation = String.class),
                                    examples = @ExampleObject(value = "Invalid or expired token"))),
            }
    )
    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(
            summary = "Test your JWT token for creator",
            description = "You can test your JWT token here for creator",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Hello Creator",
                            content = @Content(
                                    schema = @Schema(implementation = String.class),
                                    examples = @ExampleObject(value = "Hello Creator"))),
                    @ApiResponse(responseCode = "401", description = "Insufficient permissions",
                            content = @Content(
                                    schema = @Schema(implementation = String.class),
                                    examples = @ExampleObject(value = "Insufficient permissions"))),
            }
    )
    @GetMapping("/creator/test")
    public ResponseEntity<String> testAdmin() {
        return new ResponseEntity<>("Hello Creator", HttpStatus.OK);
    }
}
