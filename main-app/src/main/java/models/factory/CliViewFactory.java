package models.factory;

import view.*;

public class CliViewFactory implements ViewFactory {

    @Override
    public AuthView createAuthView() { return new CliAuthView(); }


}
