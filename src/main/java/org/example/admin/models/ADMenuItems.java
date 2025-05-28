package org.example.admin.models;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.util.List;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "menu_items")
public class ADMenuItems {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private Double price;

    private String description;

    @Setter
    @Column(name = "image_url")
    private String imageUrl;

    @Column(name ="preparation_time")
    private Integer preparationTime;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private ADCategory category;

    @Column(name = "category")
    private String categoryName;

    @OneToMany(mappedBy = "menuItem", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<ADMenuItemIngredient> menuItemIngredients;
}



