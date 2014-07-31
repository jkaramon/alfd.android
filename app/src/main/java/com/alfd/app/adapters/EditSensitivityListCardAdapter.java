package com.alfd.app.adapters;

import android.app.Activity;

import com.alfd.app.cards.EditSensitivityCard;
import com.alfd.app.cards.SensitivityCard;
import com.alfd.app.data.Product;
import com.alfd.app.data.Sensitivity;
import com.alfd.app.data.User;

import java.util.ArrayList;
import java.util.List;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardArrayAdapter;

public class EditSensitivityListCardAdapter extends CardArrayAdapter {

    private final Activity activity;

    public EditSensitivityListCardAdapter(Activity activity, List<Card> cards) {
        super(activity, cards);
        this.activity = activity;
    }


    public static List<Card> mapCards(Activity activity, Product product, List<User> users, List<Sensitivity> sensitivities) {
        List<Card> cards = new ArrayList<Card>();
        for (User u : users) {

            Sensitivity sensitivity = new Sensitivity();
            sensitivity.UserId = u.getId();
            sensitivity.ProductId = product.getId();
            sensitivity.Level = Sensitivity.Levels.UNKNOWN;
            for (Sensitivity s : sensitivities) {
                if (s.UserId == u.getId() && s.ProductId == product.getId()) {
                    sensitivity = s;
                    break;
                }
            }


            EditSensitivityCard card = new EditSensitivityCard(activity, u, product, sensitivity);
            cards.add(card);

        }
        return cards;
    }




}