package chatzis.nikolas.mc.npcsystem;

import java.util.Optional;

/**
 * This record is used to store value and signature of a players skin.
 * @author Niko
 * @since 0.0.1
 */
public record NPCSkin(String value, String signature, Optional<String> username) {

}
