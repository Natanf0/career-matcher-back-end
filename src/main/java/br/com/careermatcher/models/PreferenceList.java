package br.com.careermatcher.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PreferenceList<T> implements Comparable<PreferenceList<T>> {
    private T item;
    private int score;
    
    @Override
    public int compareTo(PreferenceList<T> other) {
        // Ordenação decrescente por score (maior score = maior preferência)
        return Integer.compare(other.score, this.score);
    }
}
