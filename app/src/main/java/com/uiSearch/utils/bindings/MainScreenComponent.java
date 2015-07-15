package com.uiSearch.utils.bindings;

import com.uiSearch.data.network.NetworkConnection;
import com.uiSearch.data.serializer.JsonDataManager;

import dagger.Component;

@Component(modules = {FragmentModule.class})
public interface MainScreenComponent {
    NetworkConnection provideNetworkConnection();
    JsonDataManager provideJsonDataManager();
}
