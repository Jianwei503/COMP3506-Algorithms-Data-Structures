import java.util.*;

public class ErdosNumbersBackup2 {
    /**
     * String representing Paul Erdos's name to check against
     */
    public static final String ERDOS = "Paul Erdös";

    private List<Author> authors;

    private Map<String, Integer> authorsIndices;

    private Map<String, String[]> papersEntries;

    private double[][] graph;

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
    public ErdosNumbersBackup2(List<String> papers) {
        authors = new ArrayList<>();
        authorsIndices = new HashMap<>();
        papersEntries = new HashMap<>();
        String[] works, writers;
        Author author;
        int index;
        for (String entry : papers) {
            works = entry.split(":|\\|");
            writers = new String[works.length - 1];
            for (int i = 1; i < works.length; ++i) {
                writers[i - 1] = works[i];
                author = new Author(works[i]);
                index = authors.indexOf(author);
                if (index == -1) {
                    author.addPaper(works[0]);
                    authorsIndices.put(works[i], authors.size());
                    authors.add(author);
                } else {
                    authors.get(index).addPaper(works[0]);
                }
            }
            papersEntries.put(works[0], writers);
        }
        numAuthors = authors.size();

        // generates co-authorship graph
        graph = new double[numAuthors][numAuthors];
        for (int i = 0; i < numAuthors; ++i) {
            for (int j = 0; j < numAuthors; ++j) {
                graph[i][j] = Double.MAX_VALUE;
            }
        }
        for (String[] names : papersEntries.values()) {
            for (int i = 0; i < names.length - 1; ++i) {
                for (int j = i + 1; j < names.length; ++j) {
                    addCoAuthorship(names[i], names[j]);
                }
            }
        }

        //********************************************************
//        System.out.print("xxxx" + "\t");
//        for (int i = 0; i < numAuthors; ++i) {
//            System.out.print(authors.get(i).name + "\t");
//        }
//        System.out.println();
//        for (int i = 0; i < numAuthors; ++i) {
//            System.out.print(authors.get(i).name + "\t");
//            for (int j = 0; j < numAuthors; ++j) {
//                if (i == j) {
//                    System.out.print("   " + "\t\t");
//                } else
//                System.out.print(graph[i][j] + "\t\t");
//            }
//            System.out.println();
//        }
        //***********************************************
    }

    /**
     * Gets all the unique papers the author has written (either solely or
     * as a co-author).
     *
     * @param author to get the papers for.
     * @return the unique set of papers this author has written.
     */
    public Set<String> getPapers(String author) {
        return authors.get(authorsIndices.get(author)).getPapers();
    }

    /**
     * Gets all the unique co-authors the author has written a paper with.
     *
     * @param author to get collaborators for
     * @return the unique co-authors the author has written with.
     */
    public Set<String> getCollaborators(String author) {
        Set<String> coAuthors = new HashSet<>();
        int index = authorsIndices.get(author);
        for (int i = 0; i < numAuthors; ++i) {
            if (graph[index][i] != Double.MAX_VALUE) {
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
        setErdosNumbersToMaxInteger();
        retrieveAllCoauthors();
        return authors.get(authorsIndices.get(author)).erdosNumber;
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
        setErdosNumbersToMaxInteger();
        retrieveAllCoauthors();
        String[] writers = papersEntries.get(paper);
        double erdosNumSum = 0;
        for (String writer : writers) {
            erdosNumSum += authors.get(authorsIndices.get(writer)).erdosNumber;
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
        // finding shortest path using Dijkstra algorithm
        setWeightedErdosNumsToMaxDouble();
        ArrayDeque<Author> dest = new ArrayDeque<>();
        Author start = authors.get(authorsIndices.get(ERDOS));
        Author goal = authors.get(authorsIndices.get(author));
        start.visited = true;
        start.weightedErdosNum = 0;
        dest.add(start);

        Set<String> names;
        Author parent, child, minimum;
        int count = 1;
        double newErdosNum;
        while (count++ < numAuthors) {
            parent = dest.getLast();
            names = getCollaborators(parent.getName());
            for (String name : names) {
                child = authors.get(authorsIndices.get(name));
                if (!child.visited) {
                    newErdosNum = parent.weightedErdosNum
                            + getEdgeWeight(parent, child);
                    if (newErdosNum < child.weightedErdosNum) {
                        child.weightedErdosNum = newErdosNum;
                    }
                }
            }
            minimum = getAuthorVsMinWeightedErdosNum();
            if (minimum == null) { // disjointed trees
                break;
            }
            if (minimum.equals(goal)) {
                setNotVisited();
                return minimum.weightedErdosNum;
            }
            minimum.visited = true;
            dest.add(minimum);
        }
        setNotVisited();
        return Double.MAX_VALUE;
    }

    /**
     * @param nameA
     * @param nameB
     */
    private void addCoAuthorship(String nameA, String nameB) {
        int indexA = authorsIndices.get(nameA);
        int indexB = authorsIndices.get(nameB);
        Author authorA = authors.get(indexA);
        Author authorB = authors.get(indexB);
        double edgeWeight = 1.0 / getNumOfCollaborativePapers(authorA, authorB);
        graph[indexA][indexB] = edgeWeight;
        graph[indexB][indexA] = edgeWeight;
    }

    /**
     * @return
     */
    private int retrieveAllCoauthors() {
        Queue<Author> queue = new ArrayDeque<>();
        Author start = authors.get(authorsIndices.get(ERDOS));
        start.visited = true;
        start.erdosNumber = 0;
        queue.add(start);

        int coAuthorCount = 0;
        Author parent, child;
        Set<String> names;
        while (!queue.isEmpty()) {
            parent = queue.remove();
            ++coAuthorCount;
            names = getCollaborators(parent.getName());
            for (String name : names) {
                child = authors.get(authorsIndices.get(name));
                if (!child.visited) {
                    child.visited = true;
                    child.erdosNumber = parent.erdosNumber + 1;
                    queue.add(child);
                }
            }
        }
        setNotVisited();
        return coAuthorCount;
    }

    /**
     *
     */
    private void setNotVisited() {
        for (int i = 0; i < numAuthors; ++i) {
            authors.get(i).visited = false;
        }
    }

    /**
     *
     */
    private void setErdosNumbersToMaxInteger() {
        for (int i = 0; i < numAuthors; ++i) {
            authors.get(i).erdosNumber = Integer.MAX_VALUE;
        }
    }

    /**
     *
     */
    private void setWeightedErdosNumsToMaxDouble() {
        for (int i = 0; i < numAuthors; ++i) {
            authors.get(i).weightedErdosNum = Double.MAX_VALUE;
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
     * @param a
     * @param b
     * @return
     */
    private double getEdgeWeight(Author a, Author b) {
        int indexA = authorsIndices.get(a.getName());
        int indexB = authorsIndices.get(b.getName());
        return graph[indexA][indexB];
    }

    /**
     * @return
     */
    private Author getAuthorVsMinWeightedErdosNum() {
        int authorIndex = -1;
        double minErdosNum = Double.MAX_VALUE;
        Author author;
        for (int i = 0; i < numAuthors; ++i) {
            author = authors.get(i);
            if (!author.visited && author.weightedErdosNum < minErdosNum) {
                authorIndex = i;
                minErdosNum = author.weightedErdosNum;
            }
        }
        if (authorIndex == -1) {
            return null;
        }
        return authors.get(authorIndex);
    }

    /**
     *
     */
    private class Author {
        //    private class Author implements Comparable<Author> {
        private final String name;
        private int erdosNumber;
        private double weightedErdosNum;
        private boolean visited;
        private Set<String> papers;

        private Author(String name) {
            this.name = name;
            this.erdosNumber = Integer.MAX_VALUE;
            this.weightedErdosNum = Double.MAX_VALUE;
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

//        @Override
//        public int compareTo(Author other) {
//            return (int) ((weightedErdosNum - other.weightedErdosNum) * 10000);
//        }

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

