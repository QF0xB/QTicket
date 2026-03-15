package de.qf0xb.qticket.auth.model.account;

import org.jspecify.annotations.NullMarked;

@NullMarked
public enum TwoFaType {
    TOTP,
    PASSKEY,
    OTHER
}
