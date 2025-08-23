package org.usul.plaiground.utils;

import java.util.Collections;
import java.util.List;
import java.util.Random;

public class RandomizerUtil {

    private final Random rng = new Random(System.currentTimeMillis());

    public <T> void shuffleCollection(List<T> collection) {
        Collections.shuffle(collection, this.rng);
    }
}
