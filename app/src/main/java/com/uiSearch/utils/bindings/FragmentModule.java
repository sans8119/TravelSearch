package com.uiSearch.utils.bindings;

import com.uiSearch.data.network.NetworkConnection;
import com.uiSearch.data.serializer.JsonDataManager;

import dagger.Module;
import dagger.Provides;

@Module
public class FragmentModule {
    @Provides
    NetworkConnection provideNetworkConnection(){
        return new NetworkConnection();
    }

    @Provides
    JsonDataManager provideJsonDataManager(){return new JsonDataManager();}
}
