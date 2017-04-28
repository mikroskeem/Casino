package eu.mikroskeem.mod.casino.config.categories;

import lombok.Getter;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

/**
 * @author Mark Vainomaa
 */
@ConfigSerializable
@Getter
public class MessagesCategory {
    @Setting
    private String enterMoney = "§9Casino: §bEnter the amount of money, which you would like to put in.\n" +
            "§bType '§ccancel§b' to cancel";

    @Setting
    private String cancel = "§9Casino: §cCancelled the money put in request.";

    @Setting
    private String cancelWrongAmount = "§9Casino: §cCancelled the money put in request, " +
            "because the entered money amount is not a valid number.";

    @Setting
    private String cancelTooLittle = "§9Casino: §cCancelled the money put in request, " +
            "because the entered money is too low.";

    @Setting
    private String cancelTooBig = "§9Casino: §cCancelled the money put in request, " +
            "because the entered money is too big.";

    @Setting
    private String notEnoughMoney = "§cSorry, you do not have enough money";

    @Setting
    private String processing = "§9Casino: §bProcessing your money, please wait 10 seconds...";
}
