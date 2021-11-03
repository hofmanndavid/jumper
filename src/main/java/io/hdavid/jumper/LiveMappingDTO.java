package io.hdavid.jumper;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class LiveMappingDTO {
    public final Integer jumperPort;
    public final String jumperTargetAddress;
    public final Integer jumperTargetPort;

    public LiveMappingDTO(String[] split) {
        this(Integer.parseInt(split[0]),
             split[1],
             Integer.parseInt(split[2])
        );
    }
}
