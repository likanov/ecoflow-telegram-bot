package com.neverdroid.ecoflow.bot.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@lombok.Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Data {
    @SerializedName("soc")
    @Expose
    private Integer soc;
    @SerializedName("remainTime")
    @Expose
    private Integer remainTime;
    @SerializedName("wattsOutSum")
    @Expose
    private Integer wattsOutSum;
    @SerializedName("wattsInSum")
    @Expose
    private Integer wattsInSum;
    private LocalDateTime dateTime;
}
