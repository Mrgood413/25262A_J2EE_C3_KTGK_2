package com.example.lab06.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "Ten san pham khong duoc de trong")
    @Column(nullable = false, length = 255)
    private String name;

    @Length(min = 0, max = 200, message = "Ten hinh anh khong qua 200 ky tu")
    @Column(length = 200)
    private String image;

    @NotNull(message = "Gia san pham khong duoc de trong")
    @Min(value = 1, message = "Gia san pham khong duoc nho hon 1")
    @Max(value = 99999999, message = "Gia san pham khong duoc lon hon 99999999")
    @Column(nullable = false)
    private Long price;

    @NotNull(message = "Danh muc khong duoc de trong")
    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;
}

