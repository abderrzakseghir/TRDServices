package m2.microservices.AccountService.domain.event.keys;

public class AccountEventKeys {
    // Convention: service.entity.action
    public static final String ACCOUNT_REGISTERED = "account.registered";
    public static final String PROFILE_UPDATED    = "account.profile.updated";
    public static final String HISTORY_PLACED    = "account.history.placed";
    public static final String HISTORY_SETTLED    = "account.history.settled";

}