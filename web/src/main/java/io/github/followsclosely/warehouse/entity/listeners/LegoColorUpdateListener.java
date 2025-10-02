package io.github.followsclosely.warehouse.entity.listeners;

import org.hibernate.event.spi.PostCollectionUpdateEvent;
import org.hibernate.event.spi.PostCollectionUpdateEventListener;
import org.springframework.stereotype.Component;

@Component
public class LegoColorUpdateListener implements PostCollectionUpdateEventListener {

    @Override
    public void onPostUpdateCollection(PostCollectionUpdateEvent event) {
    }
}