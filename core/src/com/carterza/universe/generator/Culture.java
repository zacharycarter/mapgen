package com.carterza.universe.generator;

import com.badlogic.gdx.graphics.Color;

import java.util.*;
import java.util.concurrent.ConcurrentSkipListSet;

public class Culture
{
    public static class Language
    {
        public static class Phonotactic
        {
            private static class Phonology
            {
                public static class Phoneme
                {
                }
                public static class Vowel extends Phoneme
                {
                    public enum Height {Close, NearClose, CloseMid, Mid, OpenMid, NearOpen, Open};
                    public enum Backness {Front, NearFront, Central, NearBack, Back};

                    private Height height;
                    private Backness backness;
                    private boolean rounded;
                    private boolean nasal;
                    public Vowel(Height height, Backness backness, boolean rounded, boolean nasal)
                    {
                        this.height = height;
                        this.backness = backness;
                        this.rounded = rounded;
                        this.nasal = nasal;
                    }
                    public Height getHeight()
                    {
                        return height;
                    }
                    public Backness getBackness()
                    {
                        return backness;
                    }
                    public static Vowel getRandom()
                    {
                        final List<Height> heights = Arrays.asList(Height.values());
                        final List<Backness> backnesses = Arrays.asList(Backness.values());
                        return new Vowel(
                                heights.get((int) Util.random(0, heights.size())),
                                backnesses.get((int) Util.random(0, backnesses.size())),
                                Util.random(0, 2) > 1,
                                Util.random(0, 2) > 1);
                    }
                    public static Vowel getRelated(final Vowel vowel)
                    {
                        // Todo fix.
                        return getRandom();
                    }
                }
                public static class Consonant extends Phoneme implements Comparable
                {
                    // http://clas.mq.edu.au/phonetics/phonology/syllable/syll_phonotactic.html
                    public enum Type
                    {
                        Plosive(1), Fricative(2), Tap(3), Trill(4), Nasal(5), LateralFlap(6),
                        LateralFricative(7), LateralApproximant(8), Approximant(9);

                        private final int sonority;
                        Type(int sonority){this.sonority = sonority;}
                        public int getSonority(){return sonority;}
                        public static Set<Type> bySonority()
                        {
                            return EnumSet.allOf(Type.class);
                        }
                        public NavigableSet<Type> getHigherSonority()
                        {
                            return new ConcurrentSkipListSet<Type>(bySonority()).tailSet(this);
                        }
                        public NavigableSet<Type> getLowerSonority()
                        {
                            return new ConcurrentSkipListSet<Type>(bySonority()).headSet(this);
                        }
                    };

                    public enum Location {Bilabial, Labiodental, Dental, Alveolar, Palatoalveolar, Retroflex,
                        Alveolopalatal, Palatal, Velar, Uvular, Pharyngeal, Epiglottal, Glottal};

                    private Type type;
                    private Location location;

                    public Consonant(final Type type, final Location location)
                    {
                        this.type = type;
                        this.location = location;
                    }

                    public Type getType()
                    {
                        return type;
                    }

                    public Location getLocation()
                    {
                        return location;
                    }

                    static Type getRandomType()
                    {
                        final List<Type> types = Arrays.asList(Type.values());
                        final Type type = types.get((int) Util.random(0, types.size()));
                        return type;
                    }

                    static Location getRandomLocation()
                    {
                        final List<Location> locations = Arrays.asList(Location.values());
                        final Location location = locations.get((int) Util.random(0, locations.size()));
                        return location;
                    }

                    static Consonant getRandom()
                    {
                        return new Consonant(getRandomType(), getRandomLocation());
                    }

                    static Consonant getRelated(final Consonant consonant)
                    {
                        // 50-50
                        if (Util.random(0, 2) > 1)
                        {
                            // Change in type
                            while (true)
                            {
                                final Type type = getRandomType();
                                if (isPossible(type, consonant.getLocation()))
                                {
                                    return new Consonant(type, consonant.getLocation());
                                }
                            }
                        }
                        else
                        {
                            // Change in location
                            while (true)
                            {
                                final Location location = getRandomLocation();
                                if (isPossible(consonant.getType(), location))
                                {
                                    return new Consonant(consonant.getType(), location);
                                }
                            }
                        }
                    }

                    // http://upload.wikimedia.org/wikipedia/commons/1/15/IPA_chart_2005.png
                    static boolean isPossible(Type type, Location location)
                    {
                        if (Arrays.asList(Type.LateralFricative, Type.LateralApproximant, Type.LateralFlap)
                                .contains(type) && Arrays.asList(Location.Bilabial, Location.Labiodental).contains(location))
                        {
                            return false;
                        }
                        else if (Arrays.asList(Type.Tap, Type.Trill).contains(type) &&
                                location.equals(Location.Velar))
                        {
                            return false;
                        }
                        else if (Arrays.asList(Location.Pharyngeal, Location.Epiglottal, Location.Glottal).contains(location)
                                && (Arrays.asList(Type.Nasal, Type.LateralFricative, Type.LateralApproximant, Type.LateralFlap)).contains(type))
                        {
                            return false;
                        }
                        else if (Arrays.asList(Type.Tap, Type.Trill).contains(type) &&
                                Arrays.asList(Location.Pharyngeal, Location.Glottal).contains(location))
                        {
                            return false;
                        }
                        else if (type.equals(Type.Plosive) && Arrays.asList(Location.Pharyngeal, Location.Glottal).contains(location))
                        {
                            return false;
                        }
                        else
                        {
                            return true;
                        }
                    }
                    public int compareTo(Object t)
                    {
                        if (t instanceof Consonant)
                        {
                            final Consonant consonant = (Consonant) t;
                            return this.getType().compareTo(consonant.getType());
                        }
                        else
                        {
                            return 0;
                        }
                    }
                }
                // A phonology is a collection of phonemes.
                private final Set<Consonant> consonantInventory;
                private final Set<Vowel> vowelInventory;

                public Phonology(Set<Vowel> vowelInventory, Set<Consonant> consonantInventory)
                {
                    this.vowelInventory = vowelInventory;
                    this.consonantInventory = consonantInventory;
                }
                public static Phonology getRandom()
                {
                    final Set<Consonant> consonantInventory = new ConcurrentSkipListSet<Consonant>();
                    final Set<Vowel> vowelInventory = new ConcurrentSkipListSet<Vowel>();
                    // Create a random, but self-consistent phonology
                    final int numConsonants = (int)Util.random(5, 22);
                    final int numVowels = (int)Util.random(5, 22);

                    for (int i = 0; i<5; i++)
                    {
                        consonantInventory.add(Consonant.getRandom());
                    }

                    for (int i = 0; i<2; i++)
                    {
                        vowelInventory.add(Vowel.getRandom());
                    }

                    while (consonantInventory.size() < numConsonants)
                    {
                        consonantInventory.add(
                                Consonant.getRelated(
                                        (new ArrayList<Consonant>(consonantInventory)).
                                                get((int) Util.random(0, consonantInventory.size()))));
                    }

                    for (int i = vowelInventory.size(); i < numVowels; i++)
                    {
                        vowelInventory.add(Vowel.getRandom());
                    }
                    return new Phonology(vowelInventory, consonantInventory);
                }
                public static Phonology getEnglish()
                {
                    return new Phonology(
                            new HashSet<Vowel>(Arrays.asList(
                                    new Vowel(Vowel.Height.Close, Vowel.Backness.Front, false, false),
                                    new Vowel(Vowel.Height.Mid, Vowel.Backness.Front, false, false),
                                    new Vowel(Vowel.Height.Open, Vowel.Backness.Front, false, false),
                                    new Vowel(Vowel.Height.Mid, Vowel.Backness.Central, false, false),
                                    new Vowel(Vowel.Height.Close, Vowel.Backness.Back, false, false),
                                    new Vowel(Vowel.Height.Mid, Vowel.Backness.Back, false, false),
                                    new Vowel(Vowel.Height.Open, Vowel.Backness.Back, false, false)
                            )),
                            new HashSet<Consonant>(Arrays.asList(
                                    new Consonant(Consonant.Type.Nasal, Consonant.Location.Bilabial),
                                    new Consonant(Consonant.Type.Plosive, Consonant.Location.Bilabial),
                                    new Consonant(Consonant.Type.Nasal, Consonant.Location.Alveolar),
                                    new Consonant(Consonant.Type.Plosive, Consonant.Location.Alveolar),
                                    new Consonant(Consonant.Type.Nasal, Consonant.Location.Velar),
                                    new Consonant(Consonant.Type.Plosive, Consonant.Location.Velar),
                                    new Consonant(Consonant.Type.Fricative, Consonant.Location.Labiodental),
                                    new Consonant(Consonant.Type.Fricative, Consonant.Location.Dental),
                                    new Consonant(Consonant.Type.Fricative, Consonant.Location.Alveolar),
                                    new Consonant(Consonant.Type.Fricative, Consonant.Location.Alveolopalatal),
                                    new Consonant(Consonant.Type.Approximant, Consonant.Location.Alveolopalatal),
                                    new Consonant(Consonant.Type.Approximant, Consonant.Location.Velar),
                                    new Consonant(Consonant.Type.Approximant, Consonant.Location.Palatal),
                                    new Consonant(Consonant.Type.Fricative, Consonant.Location.Velar),
                                    new Consonant(Consonant.Type.Approximant, Consonant.Location.Alveolar),
                                    new Consonant(Consonant.Type.LateralApproximant, Consonant.Location.Alveolar))));
                }
                public Vowel getRandomVowel()
                {
                    return new ArrayList<Vowel>(vowelInventory).get((int) Util.random(0, vowelInventory.size()));
                }
                public Consonant getRandomConsonant()
                {
                    return new ArrayList<Consonant>(consonantInventory).get((int) Util.random(0, consonantInventory.size()));
                }
            }

            //private final Phonology phonology = Phonology.getRandom();
            private final Phonology phonology = Phonology.getEnglish();

            private final int minInitialConsonants = (int) Util.random(1, 3);
            private final int minFinalConsonants = (int) Util.random(1, 3);
            private final int expectedInitialConsonants = 2;
            private final int expectedFinalConsonants = 2;
            private final int maxInitialConsonants = (int) Util.random(1, 3);
            private final int maxFinalConsonants = (int) Util.random(1, 3);

            public List<Phonology.Phoneme> getRandomWord()
            {
                final int len = Math.max((int) Util.poissonRandom(2), 1);
                final List<Phonology.Phoneme> phonemes = new ArrayList<Phonology.Phoneme>();

                for (int i = 0; i < len; i++)
                {
                    final LinkedList<Phonology.Phoneme> syllable = new LinkedList<Phonology.Phoneme>();
                    final int numInitialConsonants =
                            Math.max(Math.min((int) Util.poissonRandom(expectedInitialConsonants),
                                    maxInitialConsonants), minInitialConsonants);
                    final int numFinalConsonants =
                            Math.max(Math.min((int) Util.poissonRandom(expectedFinalConsonants),
                                    maxFinalConsonants), minFinalConsonants);

                    final Phonology.Vowel vowel = phonology.getRandomVowel();

                    final Map<Phonology.Consonant.Type, Phonology.Consonant> initialConsonants =
                            new EnumMap<Phonology.Consonant.Type, Phonology.Consonant>(Phonology.Consonant.Type.class);
                    final Map<Phonology.Consonant.Type, Phonology.Consonant> finalConsonants =
                            new EnumMap<Phonology.Consonant.Type, Phonology.Consonant>(Phonology.Consonant.Type.class);
                    while (initialConsonants.size() < numInitialConsonants)
                    {
                        final Phonology.Consonant consonant = phonology.getRandomConsonant();
                        initialConsonants.put(consonant.getType(), consonant);
                    }
                    phonemes.add(vowel);
                    while (finalConsonants.size() < numFinalConsonants)
                    {
                        final Phonology.Consonant consonant = phonology.getRandomConsonant();
                        finalConsonants.put(consonant.getType(), consonant);
                    }
                    syllable.addAll(initialConsonants.values());
                    syllable.add(vowel);
                    syllable.addAll(new ConcurrentSkipListSet<Phonology.Consonant>(finalConsonants.values()).descendingSet());
                    phonemes.addAll(syllable);
                }
                return phonemes;
            }
        }
        private enum Use {CityName, Cuisine, Celebration};
        private enum WritingSystem {Logographic, Syllabic, Alphabetic, Abugida, Abjad, Featural};

        private Map<Use, Phonotactic> vocabulary = new HashMap<Use, Phonotactic>();
        private WritingSystem writingSystem;
    }
    public static class Romanisation
    {
        private static String romanise(final Language.Phonotactic.Phonology.Vowel vowel)
        {
            if (Arrays.asList(Language.Phonotactic.Phonology.Vowel.Height.Close,
                    Language.Phonotactic.Phonology.Vowel.Height.NearClose).contains(vowel.getHeight()))
            {
                if (Arrays.asList(Language.Phonotactic.Phonology.Vowel.Backness.Front,
                        Language.Phonotactic.Phonology.Vowel.Backness.NearFront).contains(vowel.getBackness()))
                {
                    return "i";
                }
                else if (Arrays.asList(Language.Phonotactic.Phonology.Vowel.Backness.Back,
                        Language.Phonotactic.Phonology.Vowel.Backness.NearBack).contains(vowel.getBackness()))
                {
                    return "u";
                }
                else
                {
                    return "e";
                }
            }
            else if (Arrays.asList(Language.Phonotactic.Phonology.Vowel.Height.CloseMid,
                    Language.Phonotactic.Phonology.Vowel.Height.Mid,
                    Language.Phonotactic.Phonology.Vowel.Height.OpenMid).contains(vowel.getHeight()))
            {
                if (Arrays.asList(Language.Phonotactic.Phonology.Vowel.Backness.Back,
                        Language.Phonotactic.Phonology.Vowel.Backness.NearBack).contains(vowel.getBackness()))
                {
                    return "o";
                }
                else
                {
                    return "e";
                }
            }
            else
            {
                return "a";
            }
        }
        private static String romanise(final Language.Phonotactic.Phonology.Consonant consonant)
        {
            if (consonant.getType().equals(Language.Phonotactic.Phonology.Consonant.Type.Nasal))
            {
                if (consonant.getLocation().equals(Language.Phonotactic.Phonology.Consonant.Location.Bilabial))
                {
                    return "m";
                }
                else if (consonant.getLocation().equals(Language.Phonotactic.Phonology.Consonant.Location.Alveolar))
                {
                    return "n";
                }
                else if (consonant.getLocation().equals(Language.Phonotactic.Phonology.Consonant.Location.Velar))
                {
                    return "ng";
                }
                else
                {
                    return "hnh";
                }
            }
            else if (consonant.getType().equals(Language.Phonotactic.Phonology.Consonant.Type.Plosive))
            {
                if (consonant.getLocation().equals(Language.Phonotactic.Phonology.Consonant.Location.Bilabial))
                {
                    return "p";
                }
                else if (consonant.getLocation().equals(Language.Phonotactic.Phonology.Consonant.Location.Alveolar))
                {
                    return "t";
                }
                else if (consonant.getLocation().equals(Language.Phonotactic.Phonology.Consonant.Location.Velar))
                {
                    return "k";
                }
                else
                {
                    return "'";
                }
            }
            else if (consonant.getType().equals(Language.Phonotactic.Phonology.Consonant.Type.Fricative))
            {
                if (consonant.getLocation().equals(Language.Phonotactic.Phonology.Consonant.Location.Labiodental))
                {
                    return "f";
                }
                else if (consonant.getLocation().equals(Language.Phonotactic.Phonology.Consonant.Location.Dental))
                {
                    return "th";
                }
                else if (consonant.getLocation().equals(Language.Phonotactic.Phonology.Consonant.Location.Alveolar))
                {
                    return "s";
                }
                else if (consonant.getLocation().equals(Language.Phonotactic.Phonology.Consonant.Location.Velar))
                {
                    return "x";
                }
                else if (consonant.getLocation().equals(Language.Phonotactic.Phonology.Consonant.Location.Glottal))
                {
                    return "h";
                }
                else
                {
                    return "s";
                }
            }
            else if (consonant.getType().equals(Language.Phonotactic.Phonology.Consonant.Type.Approximant))
            {
                if (consonant.getLocation().equals(Language.Phonotactic.Phonology.Consonant.Location.Alveolar))
                {
                    return "r";
                }
                if (consonant.getLocation().equals(Language.Phonotactic.Phonology.Consonant.Location.Palatal))
                {
                    return "j";
                }
                if (consonant.getLocation().equals(Language.Phonotactic.Phonology.Consonant.Location.Velar))
                {
                    return "w";
                }
                else
                {
                    return "h";
                }
            }
            else if (consonant.getType().equals(Language.Phonotactic.Phonology.Consonant.Type.LateralApproximant))
            {
                if (consonant.getLocation().equals(Language.Phonotactic.Phonology.Consonant.Location.Alveolar))
                {
                    return "l";
                }
                else
                {
                    return "b";
                }
            }
            else
            {
                return "?";
            }
        }
        public static String romanise(List<Language.Phonotactic.Phonology.Phoneme> phonemes)
        {
            StringBuilder builder = new StringBuilder();
            for (final Language.Phonotactic.Phonology.Phoneme phoneme : phonemes)
            {
                if (phoneme instanceof Language.Phonotactic.Phonology.Vowel)
                {
                    builder.append(romanise((Language.Phonotactic.Phonology.Vowel) phoneme));
                }
                else if (phoneme instanceof Language.Phonotactic.Phonology.Consonant)
                {
                    builder.append(romanise((Language.Phonotactic.Phonology.Consonant) phoneme));
                }
            }
            return builder.toString();
        }
    }
    public static class IPAification
    {
        private static String romanise(final Language.Phonotactic.Phonology.Vowel vowel)
        {
            return "";
        }
        private static String romanise(final Language.Phonotactic.Phonology.Consonant consonant)
        {
            return "";
        }
        public static String romanise(List<Language.Phonotactic.Phonology.Phoneme> phonemes)
        {
            return "";
        }
    }
    public static abstract class Artifact
    {
        abstract String getDescription();
        abstract String getName();
    }
    public static class Cuisine extends Artifact
    {

        @Override
        String getDescription() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        String getName() {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }
    public static class Literature extends Artifact
    {

        @Override
        String getDescription() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        String getName() {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }
    public static class VisualArt extends Artifact
    {

        @Override
        String getDescription() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        String getName() {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }
    public static class PerformingArt extends Artifact
    {

        @Override
        String getDescription() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        String getName() {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }
    public static class Celebration extends Artifact
    {

        @Override
        String getDescription() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        String getName() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

    }
    public static class Entertainment extends Artifact
    {

        @Override
        String getDescription() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        String getName() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

    }
    public static class Norm
    {

    }
    public static class Value
    {

    }
    public static class Belief
    {

    }
    public static class SocialCollective
    {

    }
    public static class Role
    {

    }

    private Language language;
    private List<Entertainment> entertainments = new ArrayList<Entertainment>();
    private List<Norm> norms = new ArrayList<Norm>();
    private List<Value> values = new ArrayList<Value>();
    private List<Belief> beliefs = new ArrayList<Belief>();
    private List<SocialCollective> socialCollective = new ArrayList<SocialCollective>();
    private List<Role> roles = new ArrayList<Role>();

    private final String name;
    private final Color color;



    public Culture (final String name, final Color color)
    {
        this.name = name;
        this.color = color;
    }

    public String getName()
    {
        return name;
    }

    public Color getColor()
    {
        return color;
    }
}
