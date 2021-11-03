package io.hdavid.jumper;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;

import java.net.Socket;

@AllArgsConstructor
@EqualsAndHashCode
public class JumperSocketDTO {
    transient public final Socket orig;
    public final String remoteHostAndPort;
}
