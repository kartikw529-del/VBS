package com.vbs.demo.controller;

import com.vbs.demo.dto.DisplayDto;
import com.vbs.demo.dto.LoginDto;
import com.vbs.demo.dto.UpdateDto;
import com.vbs.demo.models.History;
import com.vbs.demo.models.Transaction;
import com.vbs.demo.models.User;
import com.vbs.demo.repositories.HistoryRepo;
import com.vbs.demo.repositories.TransactionRepo;
import com.vbs.demo.repositories.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
public class UserController {
    @Autowired
    UserRepo userRepo;
    @Autowired
    private HistoryRepo historyRepo;
    @Autowired
    TransactionRepo transactionRepo;

    @PostMapping("/register")
    public String register(@RequestBody User user)
    {
        userRepo.save(user);
        return "Signup successful";
    }

@PostMapping("/login")
    public String login(@RequestBody LoginDto u)
    {

        User user=userRepo.findByUsername(u.getUsername());
        if(user==null) {
            return "User not found";
        }
        if (!u.getPassword().equals(user.getPassword())) {
            return "password Incorrect";
        }
        if (!u.getRole().equals(user.getRole()))
        {
            return "Role Incorrect";

        }
        return String.valueOf(user.getId());
    }
    @GetMapping("/get-details/{id}")
    public DisplayDto display (@PathVariable  int id)
    {
        User user = userRepo.findById(id).orElseThrow(()->new RuntimeException("User not found"));
                DisplayDto displayDto = new DisplayDto();
        displayDto.setUsername(user.getUsername());
        displayDto.setBalance(user.getBalance());
        return displayDto;

    }
    @PostMapping("/update")
    public String update(@RequestBody UpdateDto obj)
    {
        User user = userRepo.findById(obj.getId()).orElseThrow(()->new RuntimeException("Not found"));
        if(obj.getKey().equalsIgnoreCase("name"))
        {
            if(user.getName().equals(obj.getValue())) return "Cannot Be same";
            user.setName(obj.getValue());
        }
        else if (obj.getKey().equalsIgnoreCase("password"))
        {
            if(user.getPassword().equals(obj.getValue())) return "Cannot Be same";
            user.setPassword(obj.getValue());
        }
        else if (obj.getKey().equalsIgnoreCase("email"))
        {
            if(user.getEmail().equals(obj.getValue())) return "Cannot Be same";
            User user2 = userRepo.findByEmail(obj.getValue());
            if(user2 !=null) return "Email Already Exist";
            user.setEmail(obj.getValue());
        }

        else
        {
            return "Invalid key";
        }
        userRepo.save(user);
        return "Updated Successfully";
    }
    @PostMapping("/add/{adminId}")
    public String add(@RequestBody User user, @PathVariable int adminId)
    {
        History h1 = new History();
        h1.setDescription("Admin"+adminId+"Created User"+user.getUsername());
        historyRepo.save(h1);

        userRepo.save(user);
        if (user.getBalance()>0)
        {
            Transaction t = new Transaction();
            t.setAmount(user.getBalance());
            t.setCurrBalance(user.getBalance());
            t.setDescription("Rs" + user.getBalance() + "Deposit Successful");
            User user2 = userRepo.findByUsername(user.getUsername());
            t.setUserId(user.getId());
            transactionRepo.save(t);


        }
        return "Added Successfully";
    }

    @GetMapping("/users")
    public List<User> getAllUsers(@RequestParam String sortBy,@RequestParam String order) {
        Sort sort;
        if (order.equalsIgnoreCase("desc"))
        {
            sort = Sort.by(sortBy).descending();
        }
        else
        {
            sort = Sort.by(sortBy).ascending();

        }
        return userRepo.findAllByRole("customer",sort);
    }
    @GetMapping("/users/{keyword}")
    public String getUsers(@PathVariable String keyword)
    {
        return userRepo.findByUsernameContainingIgnoreCaseAndRole(keyword,"customer");
    }
    @DeleteMapping("delete-user/{userId}/admin/{adminId}")
    public  String delete(@PathVariable int userId,@PathVariable int adminId)
    {
        User user = userRepo.findById(userId)
                .orElseThrow(()->new RuntimeException("Not Found"));
        if (user.getBalance()>0)
        {
            return "Balance Should be zero";
        }
        userRepo.delete(user);

        History h1 = new History();
        h1.setDescription("Admin"+adminId+"Deleted User"+user.getUsername());
        historyRepo.save(h1);


        return "User Deleted Successfully";

    }

}
