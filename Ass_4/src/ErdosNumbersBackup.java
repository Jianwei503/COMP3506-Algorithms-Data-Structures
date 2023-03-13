import java.util.*;

public class ErdosNumbersBackup {
    /**
     * String representing Paul Erdos's name to check against
     */
    public static final String ERDOS = "Paul Erdös";

    private List<Author> authors;

    private Map<String, Author[]> entries;

    private int[][] coAuthorship;

    private int numAuthors;

    /**
     * Initialises the class with a list of papers and authors.
     *
     * Each element in 'papers' corresponds to a String of the form:
     * 
     * [paper name]:[author1][|author2[|...]]]
     *
     * Note that for this constructor and the below methods, authors and papers
     * are unique (i.e. there can't be multiple authors or papers with the exact same name or title).
     * 
     * @param papers List of papers and their authors
     */
    public ErdosNumbersBackup(List<String> papers) {
        authors = new ArrayList<>();
        entries = new HashMap<>();
        String[] works;
        Author[] writers;
        Author author;
        for (String entry : papers) {
            works = entry.split(":|\\|");
            writers = new Author[works.length - 1];
            for (int i = 1; i < works.length; ++i) {
                author = new Author(works[i]);
                writers[i - 1] = author;
                if (!authors.contains(author)) {
                    author.addPaper(works[0]);
                    authors.add(author);
                } else {
                    authors.get(authors.indexOf(author)).addPaper(works[0]);
                }
            }
            entries.put(works[0], writers);
        }
        numAuthors = authors.size();

        // generates co-authorship matrix
        coAuthorship = new int[numAuthors][numAuthors];
        for (int i = 0; i < numAuthors; ++i) {
            for (int j = 0; j < numAuthors; ++j) {
                coAuthorship[i][j] = 0;
            }
        }
        for (Author[] authors : entries.values()) {
            for (int i = 0; i < authors.length - 1; ++i) {
                for (int j = i + 1; j < authors.length; ++j) {
                    addCoAuthorship(authors[i], authors[j]);
                }
            }
        }

    }

    /**
     * Gets all the unique papers the author has written (either solely or
     * as a co-author).
     *
     * @param author to get the papers for.
     * @return the unique set of papers this author has written.
     */
    public Set<String> getPapers(String author) {
        Author newAuthor = new Author(author);
        return authors.get(authors.indexOf(newAuthor)).getPapers();
    }

    /**
     * Gets all the unique co-authors the author has written a paper with.
     *
     * @param author to get collaborators for
     * @return the unique co-authors the author has written with.
     */
    public Set<String> getCollaborators(String author) {
        Set<String> coAuthors = new HashSet<>();
        Author newAuthor = new Author(author);
        int index = authors.indexOf(newAuthor);
        for (int i = 0; i < numAuthors; ++i) {
            if (coAuthorship[index][i] == 1) {
                coAuthors.add(authors.get(i).getName());
            }
        }
        return coAuthors;
    }

    /**
     * Checks if Erdos is connected to all other author's given as input to
     * the class constructor.
     *
     * In other words, does every author in the dataset have an Erdos number?
     *
     * @return the connectivity of Erdos to all other authors.
     */
    public boolean isErdosConnectedToAll() {
        return retrieveAllCoauthors() == numAuthors;
    }

    /**
     * Calculate the Erdos number of an author.
     *
     * This is defined as the length of the shortest path on a graph of paper
     * collaborations (as explained in the assignment specification).
     *
     * If the author isn't connected to Erdos (and in other words, doesn't have
     * a defined Erdos number), returns Integer.MAX_VALUE.
     *
     * Note: Erdos himself has an Erdos number of 0.
     *
     * @param author to calculate the Erdos number of
     * @return authors' Erdos number or otherwise Integer.MAX_VALUE
     */
    public int calculateErdosNumber(String author) {
        if (author.equals(ERDOS)) {
            return 0;
        }
        resetErdosNumbersToMaxInteger();
        retrieveAllCoauthors();
        return (int)authors.get(authors.indexOf(new Author(author))).erdosNumber;
    }

    /**
     * Gets the average Erdos number of all the authors on a paper.
     * If a paper has just a single author, this is just the author's Erdos number.
     *
     * Note: Erdos himself has an Erdos number of 0.
     *
     * @param paper to calculate it for
     * @return average Erdos number of paper's authors
     */
    public double averageErdosNumber(String paper) {
        resetErdosNumbersToMaxInteger();
        retrieveAllCoauthors();
        Author[] writers = entries.get(paper);
        double erdosNumSum = 0;
        for (Author writer : writers) {
            erdosNumSum += authors.get(authors.indexOf(writer)).erdosNumber;
        }
        return erdosNumSum / writers.length;
    }

    /**
     * Calculates the "weighted Erdos number" of an author.
     *
     * If the author isn't connected to Erdos (and in other words, doesn't have
     * an Erdos number), returns Double.MAX_VALUE.
     *
     * Note: Erdos himself has a weighted Erdos number of 0.
     *
     * @param author to calculate it for
     * @return author's weighted Erdos number
     */
    public double calculateWeightedErdosNumber(String author) {
        if (author.equals(ERDOS)) {
            return 0;
        }
        resetErdosNumbersToMaxDouble();

        // finding shortest path using Dijkstra
        PriorityQueue<Author> queue = new PriorityQueue<>();
        Author start = authors.get(authors.indexOf(new Author(ERDOS)));
        Author goal = authors.get(authors.indexOf(new Author(author)));
//        start.visited = true;
        start.erdosNumber = 0;
        queue.add(start);

        Author parent;
        Author child;
        Set<String> names;
        double edgeWeight;
        Iterator iterator = queue.iterator();
        while (iterator.hasNext()) {
            parent = queue.remove();
            if (parent.equals(goal)) {
//                resetVisitingInfo();
                return parent.erdosNumber;
            }
            names = getCollaborators(parent.getName());
            for (String name : names) {
                child = authors.get(authors.indexOf(new Author(name)));
                edgeWeight = 1 / getNumOfCollaborativePapers(parent, child);
//                if (!child.visited) {
//                    child.visited = true;
//                    edgeWeight = 1 / getNumOfCollaborativePapers(parent, child);
//                    child.erdosNumber = parent.erdosNumber + edgeWeight;
//                    queue.add(child);
//                }
            }
        }
        resetVisitingInfo();
        return Double.MAX_VALUE;
    }

    /**
     * @param a
     * @param b
     */
    private void addCoAuthorship(Author a, Author b) {
        int indexA = authors.indexOf(a);
        int indexB = authors.indexOf(b);
        coAuthorship[indexA][indexB] = 1;
        coAuthorship[indexB][indexA] = 1;
    }

    /**
     * @return
     */
    private int retrieveAllCoauthors() {
//        Map<Author, Integer> entries = new HashMap<>();
//        Queue<Author> queue = new ArrayDeque<>();
//        Author start = authors.get(authors.indexOf(new Author(ERDOS)));
//        start.visited = true;
//        queue.add(start);
//        entries.put(start, 0);
//        start.erdosNumber = 0;
//
//        int coAuthorCount = 0;
//        Author parent;
//        Author child;
//        Set<String> names;
//        while (!queue.isEmpty()) {
//            parent = queue.remove();
//            ++coAuthorCount;
//            names = getCollaborators(parent.getName());
//            for (String name : names) {
//                child = authors.get(authors.indexOf(new Author(name)));
//                if (!child.visited) {
//                    child.visited = true;
//                    queue.add(child);
//                    entries.put(child, entries.get(parent) + 1);
//                    child.erdosNumber = entries.get(child);
//                }
//            }
//        }
//        resetVisitingInfo();
//        return coAuthorCount;
        Queue<Author> queue = new ArrayDeque<>();
        Author start = authors.get(authors.indexOf(new Author(ERDOS)));
        start.visited = true;
        start.erdosNumber = 0;
        queue.add(start);

        int coAuthorCount = 0;
        Author parent;
        Author child;
        Set<String> names;
        while (!queue.isEmpty()) {
            parent = queue.remove();
            ++coAuthorCount;
            names = getCollaborators(parent.getName());
            for (String name : names) {
                child = authors.get(authors.indexOf(new Author(name)));
                if (!child.visited) {
                    child.visited = true;
                    child.erdosNumber = parent.erdosNumber + 1;
                    queue.add(child);
                }
            }
        }
        resetVisitingInfo();
        return coAuthorCount;
    }

    /**
     *
     */
    private void resetVisitingInfo() {
        for (int i = 0; i < numAuthors; ++i) {
            authors.get(i).visited = false;
        }
    }

    /**
     *
     */
    private void resetErdosNumbersToMaxInteger() {
        for (int i = 0; i < numAuthors; ++i) {
            authors.get(i).erdosNumber = Integer.MAX_VALUE;
        }
    }

    /**
     *
     */
    private void resetErdosNumbersToMaxDouble() {
        for (int i = 0; i < numAuthors; ++i) {
            authors.get(i).erdosNumber = Double.MAX_VALUE;
        }
    }

    /**
     * @param a
     * @param b
     * @return
     */
    private int getNumOfCollaborativePapers(Author a, Author b) {
        int count = 0;
        Iterator<String> iterator = a.papers.iterator();
        while (iterator.hasNext()) {
            if (b.papers.contains(iterator.next())) {
                ++count;
            }
        }
        return count;
    }

    /**
     *
     */
    private class Author implements Comparable<Author> {
        private final String name;
        private double erdosNumber;
        private boolean visited;
        private Set<String> papers;

        private Author(String name) {
            this.name = name;
            this.erdosNumber = Integer.MAX_VALUE;
            this.visited = false;
            this.papers = new HashSet<>();
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (obj == this) {
                return true;
            }
            if (obj instanceof Author) {
                Author newObj = (Author)obj;
                return newObj.getName().equals(this.name);
            }
            return false;
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(name);
        }

        @Override
        public int compareTo(Author other) {
            return (int)(erdosNumber - other.erdosNumber);
        }

        private String getName() {
            return name;
        }

        private Set<String> getPapers() {
            return papers;
        }

        private void addPaper(String paper) {
            papers.add(paper);
        }
    }
}
