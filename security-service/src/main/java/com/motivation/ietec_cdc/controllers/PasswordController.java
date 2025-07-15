package com.motivation.ietec_cdc.controllers;

import com.motivation.ietec_cdc.dto.CreatePasswordRequest;
import com.motivation.ietec_cdc.dto.ResetPassword;
import com.motivation.ietec_cdc.dto.ResetPasswordRequest;
import com.motivation.ietec_cdc.models.User;
import com.motivation.ietec_cdc.services.BindingResultService;
import com.motivation.ietec_cdc.services.PasswordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;


/**
 * @author EgorBusuioc
 * 08.06.2025
 */
@Tag(name = "PasswordManagement", description = "Password management API service")
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class PasswordController {

    private final PasswordService passwordService;
    private final BindingResultService bindingResultService;

    @Operation(
            summary = "Reset your password",
            description = "Reset your password by providing your email address. " +
                    "A link will be sent to your email to reset your password.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "User email for password reset",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResetPasswordRequest.class),
                            examples = @ExampleObject(value = "{ \"email\": \"johndoe@gmail.com\" }"))),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Password reset link sent to your email.",
                            content = @Content(
                                    schema = @Schema(implementation = String.class),
                                    examples = @ExampleObject(value = "Password reset link sent to your email."))),
                    @ApiResponse(responseCode = "400", description = "Execution error",
                            content = @Content(
                                    schema = @Schema(implementation = String.class),
                                    examples = @ExampleObject(value = "User with this email does not exist\n" +
                                            "User already has a reset password token."))),
            }
    )
    @PostMapping("/reset-request")
    public ResponseEntity<String> reset(@RequestBody ResetPasswordRequest resetPasswordRequest) {

        try {
            passwordService.sendLink(resetPasswordRequest, null);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        return ResponseEntity.ok("Password reset link sent to your email.");
    }

    @Operation(
            summary = "Create a new patient account using creator rights and JWT token",
            description = "Creator who has rights " +
                    "can create a patient account with his email and some data which is provided in example request" +
                    "\nAt the moment, account can be generated only for user with CREATOR role. " +
                    "\nThis endpoint creates a patient account and sends reset link to his email, " +
                    "link has expiration time (6 hours) now, only one link can be created for user. After 6 hours this link will automatically be deleted, and user can reset his password by himself " +
                    "\nWithout right token will be an exception." +
                    "\nPassword is generated automatically and randomly, that nobody can login to the account without using reset link." ,
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "You have to provide admin name (yours), email, firstName and lastName of new user.",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CreatePasswordRequest.class),
                            examples = @ExampleObject(value = "{ \"patientCode\": \"{There must be 9-digit number}\", \"firstName\": \"John\"," +
                                    " \"lastName\": \"DoeNew\", \"email\": \"johndoe@gmail.com\" }"))),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Create password link sent to your customer email.",
                            content = @Content(
                                    schema = @Schema(implementation = String.class),
                                    examples = @ExampleObject(value = "Create password link sent to your customer email."))),
                    @ApiResponse(responseCode = "400", description = "Execution error",
                            content = @Content(
                                    schema = @Schema(implementation = String.class),
                                    examples = @ExampleObject(value = "User with this email already exists"))),
            }
    )
    @PostMapping("/create-request")
    public ResponseEntity<String> createPassword(@RequestBody CreatePasswordRequest createPasswordRequest,
                                                 @RequestHeader("X-User-Id") String creatorId) {

        try {
            passwordService.sendLink(createPasswordRequest, creatorId);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        return ResponseEntity.ok("Create password link sent to your customer email.");
    }

    @Operation(
            summary = "Change user password",
            description = "This endpoint allows you to change your password using the token from the email link. " +
                    "You must provide the token and the new password.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Checking your email link.",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResetPassword.class),
                            examples = @ExampleObject(value = "{ \"token\": \"{your token from email link}\", \"password\": \"NewPassword1!\"}"))),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Password has been successfully reset.",
                            content = @Content(
                                    schema = @Schema(implementation = String.class),
                                    examples = @ExampleObject(value = "Password has been successfully reset."))),
                    @ApiResponse(responseCode = "400", description = "Execution error",
                            content = @Content(
                                    schema = @Schema(implementation = String.class),
                                    examples = @ExampleObject(value = "Password validation errors"))),
            }
    )
    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody @Valid ResetPassword resetPassword, BindingResult bindingResult) {

        ResponseEntity<String> errorResponse = bindingResultService.getErrorMessage(bindingResult);
        if (errorResponse.getStatusCode().isError())
            return errorResponse;

        try {
            passwordService.setNewPassword(resetPassword);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        return ResponseEntity.ok("Password has been successfully reset.");
    }
}
