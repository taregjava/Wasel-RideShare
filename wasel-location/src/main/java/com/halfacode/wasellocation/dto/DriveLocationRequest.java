package com.halfacode.wasellocation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DriveLocationRequest {

    private String driveId;
    private double latitude;
    private double longitude;
}
