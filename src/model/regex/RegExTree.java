package model.regex;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.TreeSet;

import model.automaton.Automaton;
import model.automaton.State;

public class RegExTree {

    private RegEx root;
    private Set<Character> vocabulary;
    private RegEx lambda;

    public RegExTree(RegEx root) {
        this.root = root;
        lambda = new Lambda();
        linkNodes();
        vocabulary = new TreeSet<>();
        fillVocabulary(vocabulary);
    }

    @Override
    public String toString() {
        return root.toString();
    }

    private void linkNodes() {
        RegEx lastNode = root.knit();
        lastNode.setThread(lambda);
    }

    public Automaton convertToDFA() {
        Automaton dfa = new Automaton(new ArrayList<>(vocabulary));
        Queue<State> pendingStates = new LinkedList<>();
        Map<State, Set<RegEx>> compositions = new HashMap<>();
        Set<RegEx> composition = root.moveDown();
        State currentState = new State(dfa.nextLabel());

        dfa.setInitialState(currentState);
        pendingStates.add(currentState);
        compositions.put(currentState, composition);

        while (!pendingStates.isEmpty()) {

            List<State> toStates = new ArrayList<>();
            currentState = pendingStates.poll();
            composition = compositions.get(currentState);

            if (composition.contains(lambda)) {
                dfa.addAcceptingState(currentState);
            } else {
                dfa.addState(currentState);
            }

            for (Character symbol : vocabulary) {
                Set<RegEx> symbolNodes = new HashSet<>();
                for (RegEx node : composition) {
                    if (node.data.equals(symbol)) {
                        symbolNodes.add(node);
                    }
                }
                if (!symbolNodes.isEmpty()) {
                    Set<RegEx> newComposition = new HashSet<>();
                    for (RegEx node : symbolNodes) {
                        newComposition.addAll(node.moveUp());
                    }
                    boolean shouldCreateNewState = true;
                    for (Map.Entry<State, Set<RegEx>> entryComposition : compositions.entrySet()) {
                        if (newComposition.equals(entryComposition.getValue())) {
//                            dfa.addTransition(currentState, entryComposition.getKey(), symbol);
                            toStates.add(entryComposition.getKey());
                            shouldCreateNewState = false;
                            break;
                        }
                    }
                    if (shouldCreateNewState) {
                        State state = new State(dfa.nextLabel());
//                        dfa.addTransition(currentState, state, symbol);
                        toStates.add(state);
                        compositions.put(state, newComposition);
                        pendingStates.add(state);
                    }
                } else {
                    // Transition to the error state
//                    dfa.addTransition(currentState, null, symbol);
                    toStates.add(State.ERROR_STATE);
                }
            }
            dfa.addTransitions(currentState, toStates);
        }
        return dfa;
    }

    private void fillVocabulary(Set<Character> vocabulary) {
        root.fillVocabulary(vocabulary);
    }
}
