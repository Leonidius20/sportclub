package com.creo.invention.dev.tsyw.service.impl;

import com.creo.invention.dev.tsyw.dto.mapper.UserSubscriptionMapper;
import com.creo.invention.dev.tsyw.dto.usersubscription.CreateUserSubscriptionDto;
import com.creo.invention.dev.tsyw.dto.usersubscription.UserSubscriptionDto;
import com.creo.invention.dev.tsyw.exception.NotFoundException;
import com.creo.invention.dev.tsyw.model.Subscription;
import com.creo.invention.dev.tsyw.model.User;
import com.creo.invention.dev.tsyw.model.UserSubscription;
import com.creo.invention.dev.tsyw.repository.SubscriptionRepository;
import com.creo.invention.dev.tsyw.repository.UserRepository;
import com.creo.invention.dev.tsyw.repository.UserSubscriptionRepository;
import com.creo.invention.dev.tsyw.service.UserSubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserSubscriptionServiceImpl implements UserSubscriptionService {

    private final UserSubscriptionRepository userSubscriptionRepository;

    private final UserSubscriptionMapper userSubscriptionMapper;

    private final UserRepository userRepository;

    private final SubscriptionRepository subscriptionRepository;

    @Override
    public UserSubscriptionDto addSubscriptionToUser(CreateUserSubscriptionDto createUserSubscriptionDto) {
        User user = findUser(createUserSubscriptionDto.getUserId());
        Subscription subscription = findSubscription(createUserSubscriptionDto.getSubscriptionId());
        LocalDateTime startDateTime = createUserSubscriptionDto.getStartDate().atStartOfDay();
        UserSubscription userSubscription = UserSubscription.builder()
                .user(user)
                .subscription(subscription)
                .visitsNumber(subscription.getVisitsNumber())
                .startTime(startDateTime)
                .endTime(startDateTime.plusMonths(subscription.getMonthsDuration()))
                .paid(createUserSubscriptionDto.getPaid())
                .build();
        return userSubscriptionMapper.toDto(userSubscriptionRepository.save(userSubscription));
    }

    private User findUser(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("User with id %s not found", userId)));
    }

    private Subscription findSubscription(UUID subscriptionId) {
        return subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new NotFoundException(String.format("Subscription with id %s not found", subscriptionId)));
    }
}
