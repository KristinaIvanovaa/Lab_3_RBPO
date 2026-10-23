package com.example.demo.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.demo.dto.UserRequest;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.TicketRepository;

@Service
@Transactional
public class UserService {
    private final UserRepository users;
    private final TicketRepository tickets;

    public UserService(UserRepository users, TicketRepository tickets) {
        this.users = users;
        this.tickets = tickets;
    }

    @Transactional(readOnly = true)
    public List<User> findAll() { return users.findAll(); }

    @Transactional(readOnly = true)
    public User findById(Long id) { return users.findById(id).orElseThrow(() -> new ResourceNotFoundException("User", id)); }

    public User create(UserRequest request) {
        if (request.name() == null || request.email() == null) {
            throw new IllegalArgumentException("name and email are required");
        }
        return users.save(new User(request.name(), request.email()));
    }

    public User update(Long id, UserRequest request) {
        User user = findById(id);
        if (request.name() != null) user.setName(request.name());
        if (request.email() != null) user.setEmail(request.email());
        return users.save(user);
    }

    public void delete(Long id) {
        findById(id);
        tickets.deleteAll(tickets.findByUserId(id));
        users.deleteById(id);
    }
}
