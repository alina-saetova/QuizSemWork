package com.itis.kpfu.helpers;

public class DataSource {

    private static final DataSource instance = new DataSource();

    public static DataSource getInstance() {
        return instance;
    }

    private String[] animals = {("Alligator"), ("Anteater"),
            ("Armadillo"), ("Auroch"), ("Axolotl"), ("Badger"),
            ("Bat"), ("Bear"), ("Beaver"), ("Buffalo"), ("Camel"),
            ("Capybara"), ("Chameleon"), ("Cheetah"), ("Chinchilla"), ("Chipmunk"),
            ("Chupacabra"), ("Cormorant"), ("Coyote"), ("Crow"), ("Dingo"),
            ("Dinosaur"), ("Dog"), ("Dolphin"), ("Duck"), ("Elephant"), ("Ferret"),
            ("Fox"), ("Frog"), ("Giraffe"), ("Gopher"), ("Grizzly"),
            ("Hedgehog"), ("Hippo"), ("Hyena"), ("Ibex"), ("Ifrit"), ("Iguana"),
            ("Jackal"), ("Kangaroo"), ("Koala"), ("Kraken"), ("Lemur"), ("Leopard"),
            ("Liger"), ("Lion"), ("Llama"), ("Loris"), ("Manatee"), ("Minkv"),
            ("Monkey"), ("Moose"), ("Narwhal"), ("Nyan Cat"), ("Orangutan"),
            ("Otter"), ("Panda"), ("Penguin"), ("Platypus"), ("Pumpkin"),
            ("Python"), ("Quagga"), ("Rabbit"), ("Raccoon"), ("Rhino"),
            ("Sheep"), ("Shrew"), ("Skunk"), ("Squirrel"), ("Tiger"),
            ("Turtle"), ("Walrus"), ("Wolf"), ("Wolverine"), ("Wombat")};

    public String getName() {
        return animals[(int) (Math.random() * animals.length)];
    }
}
