package by.nikiforova.crud.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotEmpty(message = "Имя не может быть пустым")
    @Size(min = 2, max = 50, message = "Имя должно иметь от 2 до 50 символов")
    @Column(name = "name")
    private String name;

    @NotEmpty(message = "Email не может быть пустым")
    @Email(message = "Email должен быть валидным")
    @Column(name = "email")
    private String email;

    @Min(value = 0, message = "Возраст не может быть меньше 0")
    @Max(value = 120, message = "Возраст не может быть больше 120")
    @Column(name = "age")
    private Integer age;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public User() {
    }

    public User(String name, String email, Integer age) {
        this.name = name;
        this.email = email;
        this.age = age;
        this.createdAt = LocalDateTime.now();
    }

    public int getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        User person = (User) o;
        return id == person.id && Objects.equals(name, person.name) && Objects.equals(email, person.email) && Objects.equals(age, person.age) && Objects.equals(createdAt, person.createdAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, email, age, createdAt);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Person{");
        sb.append("id=").append(id);
        sb.append(", name='").append(name).append('\'');
        sb.append(", email='").append(email).append('\'');
        sb.append(", age=").append(age);
        sb.append(", createdAt=").append(createdAt);
        sb.append('}');
        return sb.toString();
    }
}
