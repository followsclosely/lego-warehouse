package io.github.followsclosely.warehouse;

import io.github.followsclosely.warehouse.entity.listeners.LegoColorUpdateListener;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import org.hibernate.event.service.spi.EventListenerRegistry;
import org.hibernate.event.spi.EventType;
import org.hibernate.internal.SessionFactoryImpl;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class HibernateEventConfig {

    private final EntityManagerFactory entityManagerFactory;

    private final LegoColorUpdateListener legoColorUpdateListener;

    @PostConstruct
    public void registerListeners() {
        SessionFactoryImpl sessionFactory = entityManagerFactory.unwrap(SessionFactoryImpl.class);
        EventListenerRegistry registry = sessionFactory.getServiceRegistry().getService(EventListenerRegistry.class);
        registry.getEventListenerGroup(EventType.POST_COLLECTION_UPDATE).appendListener(legoColorUpdateListener);
    }
}