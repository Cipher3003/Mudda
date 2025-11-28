/**
 * ---------------------------------------------------------------
 * Project : Mudda
 * File    : CategorySeed
 * Author  : Vikas Kumar
 * Created : 28-11-2025
 * ---------------------------------------------------------------
 */
package com.mudda.backend.category;

public record CategorySeed(
        String name
) {

    public static Category toCategory(CategorySeed seed) {
        return new Category(
                seed.name()
        );
    }
}
