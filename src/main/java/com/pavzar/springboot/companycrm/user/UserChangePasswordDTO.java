package com.pavzar.springboot.companycrm.user;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class UserChangePasswordDTO {

    @NotNull(message = "is required")
    @Size(min = 1, message = "is required")
    private String currentPassword;

    @NotNull(message = "is required")
    @Size(min = 1, message = "is required")
    private String newPassword;

    @NotNull(message = "is required")
    @Size(min = 1, message = "is required")
    private String newPasswordConfirm;

}
