package com.blackjack.server.repositories;

import com.blackjack.server.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);
    User findByEmail(String email);

    @Modifying
    @Transactional
    @Query(value = "UPDATE users " +
            "SET name = :name" +
            "WHERE id = :id",
            nativeQuery = true)
    void updateNameById(@Param("name") String name, @Param("id") Long id);

    @Modifying
    @Transactional
    @Query(value = "UPDATE users " +
            "SET password = :password"+
            "WHERE id = :id",
            nativeQuery = true)
    void updatePasswordById(@Param("password") String password, @Param("id") Long id);

    @Modifying
    @Transactional
    @Query(value = "UPDATE users " +
            "SET email = :email " +
            "WHERE id = :id",
            nativeQuery = true)
    void updateEmailById(@Param("email") String email, @Param("id") Long id);

    @Modifying
    @Transactional
    @Query(value = "UPDATE users " +
            "SET money = money + :amountToIncrease" +
            "WHERE id = :id",
            nativeQuery = true)
    void increaseMoneyById(@Param("amountToIncrease") Double amountToIncrease, @Param("id") Long id);

    @Modifying
    @Transactional
    @Query(value = "UPDATE users " +
            "SET money = money - :amountToDecrease" +
            "WHERE id = :id",
            nativeQuery = true)
    void decreaseMoneyById(@Param("amountToDecrease") Double amountToDecrease, @Param("id") Long id);

}
