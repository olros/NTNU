package ntnu.idatt2105.security.controller

import io.swagger.annotations.Api
import io.swagger.annotations.ApiParam
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import ntnu.idatt2105.core.response.Response
import ntnu.idatt2105.security.dto.*
import ntnu.idatt2105.user.dto.DetailedUserDto
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.bind.annotation.PostMapping
import java.util.*
import javax.servlet.http.HttpServletRequest

@Api(value = "Authentication services", tags = ["Authentication Services"], description = "Authentication Services")
@RequestMapping("auth/")
interface AuthenticationController {

    @Operation(summary = "Log into the application", responses = [
        ApiResponse(responseCode = "200", description = "Success: Access and refresh token generated")])
    @PostMapping("/login")
    fun fakeLogin(
        @ApiParam("Login credentials") @RequestBody credentials: LoginRequest?,
    ) {
        throw IllegalStateException("This method shouldn't be called. It's implemented by Spring Security filters.")
    }

    @Operation(summary = "Refresh an old access token", responses = [
        ApiResponse(responseCode = "200", description = "Success"),
        ApiResponse(responseCode = "400", description = "Bad request: a new refresh token could not be issued"),
    ])
    @GetMapping("/refresh-token/")
    fun refreshToken(request: HttpServletRequest): JwtTokenResponse?

    @Operation(summary = "Send an email with a link to reset password", responses = [
        ApiResponse(responseCode = "201", description = "Created: reset password email was sent"),
        ApiResponse(responseCode = "400", description = "Bad request: reset password email was not sent"),
    ])
    @PostMapping("/forgot-password/")
    fun forgotPassword(@RequestBody email: ForgotPassword): ResponseEntity<Response>

    @Operation(summary = "Reset a password", responses = [
        ApiResponse(responseCode = "201", description = "Created: password was reset"),
        ApiResponse(responseCode = "400", description = "Bad request: password was not reset"),
    ])
    @PostMapping("/reset-password/{passwordResetTokenId}/")
    fun resetPassword(@PathVariable passwordResetTokenId: UUID, @RequestBody reset: ResetPasswordDto): ResponseEntity<Response>

    @Operation(summary = "Make a user admin", responses = [
        ApiResponse(responseCode = "200", description = "Success: user was made an admin"),
        ApiResponse(responseCode = "404", description = "Not found: user was not found"),
    ])
    @PostMapping("/make-admin/")
    fun makeAdmin(@RequestBody user: MakeAdminDto): ResponseEntity<DetailedUserDto>
}
