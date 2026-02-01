package m2.microservices.AccountService.domain.port.in;

import m2.microservices.AccountService.domain.port.in.command.RegisterAccountCommand;
import m2.microservices.AccountService.domain.port.in.command.UpdateProfileCommand;

public interface ManageAccountUseCase {
    void register(RegisterAccountCommand command);

    void updateProfile(UpdateProfileCommand command);

}
