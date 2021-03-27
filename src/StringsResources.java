/**
 * Class that contains all strings showed in the user interface.
 * It supports many languages. Changing the language has O(1) and taking the string O(n) where
 * n is number of languages.
 * Until number of languages are reasonably small it's not necessary to change
 * the complexity of all getters to constant
 */
public class StringsResources {
    private static final int ENGLISH = 0;
    private static final int POLISH = 1;
    private static int language = ENGLISH;

    // menus
    static String title() { return new String[]{"Math Visualizer", "Wizualizator matematyki"}[language] + " " + Main.version; }
    static String firstSteps() { return new String[]{"First Steps", "Pierwsze kroki"}[language]; }
    static String visualizations() { return new String[]{"Visualizations", "Wizualizacje"}[language]; }
    static String theory() { return new String[]{"Theory", "Teoria"}[language]; }
    static String settings() { return new String[]{"Settings", "Ustawienia"}[language]; }
    static String exit() { return new String[]{"Exit", "Wyjście"}[language]; }
    static String matrixSimulation() { return new String[]{"Matrix Simulation", "Symulacja macierzy"}[language]; }
    static String linearRegression() { return new String[] {"Linear Regression", "Regresja liniowa"}[language]; }
    static String knn() { return new String[] {"K-Nearest Neighbours", "K-Najbliższych sąsiadów"}[language]; }
    static String logisticRegression() { return new String[]{"Logistic Regression", "Regresja logistyczna"}[language]; }
    static String PCAAlgorithm() { return new String[]{"PCA Algorithm", "Algorytm PCA"}[language]; }
    static String back() { return new String[]{"Back", "Powrót"}[language]; }
    static String linearAlgebra() { return new String[]{"Linear Algebra", "Algebra liniowa"}[language]; }
    static String calculus() { return new String[]{"Calculus", "Analiza"}[language]; }
    static String setsAndGraphsTheory() { return new String[]{"Sets & Graphs Theory", "Teoria mnogości i grafów"}[language]; }
    static String probabilisticAndStatistic() { return new String[]{"Probabilistic & Statistic", "Rachunek prawdopodobieństwa i statystyka"}[language]; }
    static String machineLearning() { return new String[]{"Machine Learning", "Uczenie maszynowe"}[language]; }
    static String vectors() { return new String[]{"Vectors","Wektory"}[language]; }
    static String matrixAsASystemOfEquation() { return new String[]{"Matrix as a system of equations", "Macierz jako układ równań"}[language]; }
    static String matricesGeometrically() { return new String[]{"Matrices geometrically", "Macierze w geometrii"}[language]; }
    static String inverseOfMatrix() { return new String[]{"Inverse of Matrix", "Macierz odwrotna"}[language]; }
    static String fourSubspacesOfMatrix() { return new String[]{"Four Subspaces of Matrix", "Cztery podprzestrzenie macierzy"}[language]; }
    static String determinant() { return new String[]{"Determinant", "Wyznacznik"}[language]; }
    static String eigenVectorsAndEigenValues() { return new String[]{"Eigenvectors & Eigenvalues", "Wektory i wartości własne"}[language]; }
    static String singularValueDecomposition() { return new String[]{"Singular Value Decomposition", "Rozkład według wartości osobliwych"}[language]; }

    // cartesian plane
    static String hide() { return new String[]{"Hide Panel", "Schowaj Panel"}[language]; }
    static String menu() { return new String[]{"Main Menu", "Menu Główne"}[language]; }
    static String help() { return new String[]{"Help", "Pomoc"}[language]; }
    static String grid() { return new String[]{"Grid", "Kratka"}[language]; }
    static String areYouSure() { return new String[]{"Are you sure you want to quit this simulation?", "Czy jesteś pewny, że chcesz wyjść z symulacji?"}[language]; }
    static String close() { return new String[]{"Close", "Zamknij"}[language]; }
    static String yes() { return new String[]{"Yes", "Tak"}[language]; }
    static String no() { return new String[]{"No", "Nie"}[language]; }

    // matrix
    static String matrixGrid() { return new String[]{"Matrix grid", "Siatka macierzy"}[language]; }
    static String transpose() { return new String[]{"Transpose", "Transpozycja"}[language]; }
    static String inverse() { return new String[]{"Inverse", "Odwrotność"}[language]; }
    static String eigenvectors() { return new String[]{"Eigenvectors", "Wektory własne"}[language]; }
    static String projected() { return new String[]{"Projected", "Zrzutowane"}[language]; }

    // linear regression
    static String line() { return new String[]{"Line", "Linia"}[language]; }
    static String errors() { return new String[]{"Errors", "Błędy"}[language]; }
    static String error() { return new String[]{"Error", "Błąd"}[language]; }

    // logistic regression
    static String weights() { return new String[]{"Weights", "Wagi"}[language]; }
    static String eta() { return "ETA"; }
    static String epochs() { return new String[]{"Epochs", "Epoki"}[language]; }
    static String bias() { return new String[]{"Bias", "Próg"}[language]; }

    //knn
    static String distances() { return new String[]{"Distances", "Odległości"}[language]; }
    static String rings() { return new String[]{"Rings", "Pierścienie"}[language]; }

    // pca
    static String covMatrix() { return new String[]{"Cov Matrix", "Macierz Kow."}[language]; }

    static void goEnglish() {
        language = ENGLISH;
    }

    static void goPolish() {
        language = POLISH;
    }
}
