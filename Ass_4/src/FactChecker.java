import java.util.*;

public class FactChecker {
    /**
     * A list used to store all of the tutors
     */
    private List<Tutor> tutors;

    /**
     * A list used to store all of the tutors who have no in-degree
     */
    private List<Tutor> noInDegreeTutors;

    /**
     * A graph using adjacency list representation to represent adjacency relation
     */
    private Map<String, List<Tutor>> graph;

    /**
     * Checks if a list of facts is internally consistent. 
     * That is, can they all hold true at the same time?
     * Or are two (or potentially more) facts logically incompatible?
     * 
     * @param facts list of facts to check consistency of
     * @return true if all the facts are internally consistent, otherwise false.
     */
    public static boolean areFactsConsistent(List<Fact> facts) {
        assert facts != null;
        if (isFactContradictory(facts)) {
            return false;
        }
        FactChecker checker = new FactChecker(facts);
        return checker.topologicalSort() == checker.getNumberOfTutors();
    }

    /**
     * Initialises the FactChecker and populates the internal data structures
     * with the given list of facts.
     *
     * @param facts to populate with
     */
    private FactChecker(List<Fact> facts) {
        graph = new HashMap<>();
        tutors = new ArrayList<>();
        noInDegreeTutors = new ArrayList<>();

        for (Fact fact : facts) {
            if (fact.getType() == Fact.FactType.TYPE_ONE) {
                handleFactType1(fact);
            } else {
                handleFactType2(fact);
            }
        }
    }

    /**
     * Checks if there is a contradiction of fact itself in the given facts
     * (e.g. Fact(TYPE_ONE, "a", "b"), Fact(TYPE_TWO, "a", "b"))
     * @param facts list of facts to check consistency of
     * @return true if there is at least one contradiction
     *         false otherwise
     */
    private static boolean isFactContradictory(List<Fact> facts) {
        Map<String, Fact.FactType> entries = new HashMap<>();
        String names;
        for (Fact fact : facts) {
            names = fact.getPersonA() + fact.getPersonB();
            if (entries.containsKey(names) &&
                    !entries.get(names).equals(fact.getType())) {
                return true;
            }
            entries.put(names, fact.getType());
        }
        return false;
    }

    /**
     * Gets the total number of tutors who get involved
     *
     * @return total number of tutors
     */
    private int getNumberOfTutors() {
        return tutors.size();
    }

    /**
     * Sorts the tutors in a linear ordering using topological sorting algorithm
     *
     * @return the number of tutors got sorted
     */
    private int topologicalSort() {
        noInDegreeTutors.addAll(getNoInDegreeTutors());
        int count = 0;
        while (!noInDegreeTutors.isEmpty()) {
            removeOutgoingEdges(noInDegreeTutors.remove(0));
            noInDegreeTutors.addAll(getNoInDegreeTutors());
            ++count;
        }
        return count;
    }

    /**
     * Handles the given type-one fact, updates the information of
     * FactChecker.tutors and FactChecker.graph
     *
     * @param fact a type-one fact to be handled
     */
    private void handleFactType1(Fact fact) {
        String nameA = fact.getPersonA();
        String nameB = fact.getPersonB();
        Tutor a = new Tutor(nameA);
        Tutor b = new Tutor(nameB);
        int index;
        if (!tutors.contains(a)) {
            tutors.add(a);
        }
        if ((index = tutors.indexOf(b)) == -1) {
            tutors.add(b);
        } else {
            b = tutors.get(index);
        }

        b.inDegree += 1;
        if (graph.containsKey(nameA)) {
            graph.get(nameA).add(b);
        } else {
            List<Tutor> tutors = new ArrayList<>();
            tutors.add(b);
            graph.put(nameA, tutors);
        }
    }

    /**
     * Handles the given type-two fact, updates the information of
     * FactChecker.tutors and FactChecker.graph
     *
     * @param fact a type-two fact to be handled
     */
    private void handleFactType2(Fact fact) {
        handleFactType1(fact);
    }

    /**
     * Gets all tutors who have zero in-degree
     *
     * @return a list of tutors with zero in-degree
     */
    private List<Tutor> getNoInDegreeTutors() {
        List<Tutor> assistants = new ArrayList<>();
        for (Tutor tutor : tutors) {
            if (tutor.inDegree == 0 && !tutor.visited) {
                assistants.add(tutor);
                tutor.visited = true;
            }
        }
        return assistants;
    }

    /**
     * Removes all outgoing edges from the given tutor
     *
     * @param tutor a tutor to be removed outgoing edges
     */
    private void removeOutgoingEdges(Tutor tutor) {
        if (graph.containsKey(tutor.name)) {
            for (Tutor assistant : graph.get(tutor.name)) {
                assistant.inDegree -= 1;
            }
            graph.remove(tutor.name);
        }
    }

    /**
     * A class represents tutor
     */
    private class Tutor {
        // the name of tutor
        private final String name;

        // the number of incoming edges to this tutor
        private int inDegree;

        // flag of visiting log
        private boolean visited;

        /**
         * Initialises the Tutor and populates the internal data structures
         * with the given name.
         *
         * @param name the name of person to be initialised
         */
        private Tutor(String name) {
            this.name = name;
            this.inDegree = 0;
            this.visited = false;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (obj == this) {
                return true;
            }
            if (obj instanceof FactChecker.Tutor) {
                FactChecker.Tutor newObj = (FactChecker.Tutor)obj;
                return newObj.name.equals(this.name);
            }
            return false;
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(name);
        }
    }
}
