INSERT INTO OWNER (id, name, age, gender) VALUES
      (1, 'Joe', 12, 'MALE'),
      (2, 'Ruan', 40, 'MALE'),
      (3, 'Mariana', 30, 'FEMALE')
;


INSERT INTO PET (id, name, age, color, type, description, owner_id, is_favourite) VALUES
    (1, 'Trinity', 6, 'Yellow', 'DOG', 'Trinity is a dog of color Yellow and is 6 years old', 3, false),
    (2, 'Sebastian', 29, 'Green', 'TURTLE', 'Sebastian is a turtle of color Green and is 29 years old', 2, false),
    (3, 'Merlin', 1, 'Gray', 'CAT', 'Merlin is a cat of color Grey and is 1 year old', 1, false),
    (4, 'Toby', 2, 'White', 'CAT', 'Toby is a cat of color White and is 2 year old', 1, true)
 ;