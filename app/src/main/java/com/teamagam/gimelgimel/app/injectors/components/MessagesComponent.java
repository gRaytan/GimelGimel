package com.teamagam.gimelgimel.app.injectors.components;


import com.teamagam.gimelgimel.app.injectors.modules.MessageModule;
import com.teamagam.gimelgimel.app.injectors.scopes.PerFragment;
import com.teamagam.gimelgimel.app.view.fragments.dialogs.SendMessageDialogFragment;

import dagger.Component;

/**
 * A scope {@link PerFragment} component.
 * Injects user specific Fragments.
 */
@PerFragment
@Component(
        dependencies = ApplicationComponent.class,
        modules = {
                MessageModule.class
        }
)
public interface MessagesComponent{
    void inject(SendMessageDialogFragment sendMessageFragment);
}
