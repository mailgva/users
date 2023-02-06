package com.horbatenko.users.to;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserTo {
    private String id;

    @NotNull
    @Size(min = 1, max = 256)
    private String name;

    @Email
    @NotNull
    private String email;
}
