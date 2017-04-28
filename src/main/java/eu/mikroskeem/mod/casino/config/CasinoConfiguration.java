package eu.mikroskeem.mod.casino.config;

import eu.mikroskeem.mod.casino.config.categories.LoseCategory;
import eu.mikroskeem.mod.casino.config.categories.MessagesCategory;
import eu.mikroskeem.mod.casino.config.categories.WaitCategory;
import eu.mikroskeem.mod.casino.config.categories.WinCategory;
import lombok.Getter;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
@Getter
public class CasinoConfiguration {
    @Setting("win-chance")
    /* Chance for winning */
    private double winChance = 0.5;

    @Setting("process-time-seconds")
    /* The number of seconds how much the casino process should took */
    private int processTime = 10;

    @Setting("sign-line-number-to-check")
    /* The checkable line number of the signs to find the casino sign (0-3) */
    private int signCheckLine = 0;

    @Setting("minimum-depositable-money")
    /* Min amount of the depositable money */
    private int minAmount = 10;

    @Setting("maximum-depositable-money")
    /* Max amount of the depositable money */
    private int maxAmount = 1000;

    @Setting("create-sign-line")
    /* The checkable line of the sign should be exactly this string for creating a casino sign */
    private String signCreateLine = "[Casino]";

    @Setting("done-sign-line")
    /* The valid casino signs signCheckLine, should be exactly this string. */
    private String signDoneLine = "§1[Casino]";

    @Setting("wait")
    private WaitCategory wait = new WaitCategory();

    @Setting("win")
    private WinCategory win = new WinCategory();

    @Setting("lose")
    private LoseCategory lose = new LoseCategory();

    @Setting("messages")
    private MessagesCategory messages = new MessagesCategory();
}
