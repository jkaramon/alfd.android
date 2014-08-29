package com.alfd.app.data;

import android.content.Context;

import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;
import com.alfd.app.R;
import com.alfd.app.utils.Utils;

import org.joda.time.DateTime;

import java.util.List;

/**
 * Created by karamon on 15. 7. 2014.
 */

@Table(name = "Sensitivities")
public class Sensitivity extends BaseServerModel {

    @Column
    public long UserId;

    @Column
    public long ProductId;



    @Column
    public String Note;

    @Column
    public String Level = Levels.UNKNOWN;




    public com.alfd.app.rest.Sensitivity toREST() {
        com.alfd.app.rest.Sensitivity restSensitivity = new com.alfd.app.rest.Sensitivity();
        restSensitivity.level = Level;
        restSensitivity.note = Note;
        restSensitivity.userId = getUserServerId(UserId);
        restSensitivity.productId = getProductServerId(ProductId);
        return restSensitivity;
    }

    private String getUserServerId(long userId) {
        User u = User.load(User.class, userId);
        return u != null ? u.ServerId : null;
    }
    private String getProductServerId(long productId) {
        Product p = Product.load(Product.class, productId);
        return p != null ? p.ServerId : null;
    }

    public static Sensitivity fromREST(com.alfd.app.rest.Sensitivity restSensitivity) {
        Sensitivity s = new Sensitivity();
        User user = User.getByServerId(User.class, restSensitivity.userId);
        Product product = Product.getByServerId(Product.class, restSensitivity.productId);
        if (user != null) {
            s.UserId = user.getId();
        }
        if (product != null) {
            s.ProductId = product.getId();
        }
        s.Note = restSensitivity.note;
        s.Level = restSensitivity.level;

        return s;
    }

    public static List<Sensitivity> getByProduct(Long productId) {
        return new Select()
                .from(Sensitivity.class)
                .where("ProductId = ?", productId)
                .execute();
    }



    public static int getLevelResourceId(Context ctx, String level) {
        if (Utils.isBlank(level)) {
            return R.drawable.s_unknown;
        }
        int resId = ctx.getResources().
                getIdentifier( "s_" + level.toLowerCase(), "drawable", ctx.getPackageName());
        if (resId == 0) {
            return R.drawable.s_unknown;
        }
        return resId;
    }




    public static class Levels {
        public static String UNKNOWN = "unknown";
        public static String NONE = "none";
        public static String LOW = "low";
        public static String HIGH = "high";

    }


}
