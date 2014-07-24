package com.alfd.app.adapters;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;

import com.alfd.app.cards.ProductCard;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardGridCursorAdapter;
import it.gmariotti.cardslib.library.internal.CardHeader;

public class ProductListCardAdapter extends CardGridCursorAdapter {

    private final Activity activity;

    public ProductListCardAdapter(Activity activity) {

        super(activity);
        this.activity = activity;
    }

    @Override
    protected Card getCardFromCursor(Cursor cursor) {
        ProductCard card = new ProductCard(activity);
        setCardFromCursor(card, cursor);

        //Create a CardHeader
        CardHeader header = new CardHeader(activity);
        //Set the header title

        header.setTitle(card.getName());

        //Add Header to card
        card.addCardHeader(header);

        return card;
    }

    private void setCardFromCursor(ProductCard card, Cursor cursor) {
        card.setFromCursor(cursor);

    }
}