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
            "SET name = :name " +
            "WHERE email = :email",
            nativeQuery = true)
    void updateNameByEmail(@Param("name") String name, @Param("email") String email);

    @Modifying
    @Transactional
    @Query(value = "UPDATE users " +
            "SET password = :password "+
            "WHERE email = :email",
            nativeQuery = true)
    void updatePasswordByEmail(@Param("password") String password, @Param("email") String email);

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
            "SET money = money + :amountToIncrease " +
            "WHERE email = :email",
            nativeQuery = true)
    void increaseMoneyByEmail(@Param("amountToIncrease") int amountToIncrease, @Param("email") String email);

    @Modifying
    @Transactional
    @Query(value = "UPDATE users " +
            "SET money = money - :amountToDecrease " +
            "WHERE email = :email",
            nativeQuery = true)
    void decreaseMoneyByEmail(@Param("amountToDecrease") Double amountToDecrease, @Param("email") String email);

}
