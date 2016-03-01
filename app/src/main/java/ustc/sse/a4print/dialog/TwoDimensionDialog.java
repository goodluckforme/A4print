package ustc.sse.a4print.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import ustc.sse.a4print.R;

/**
 * Created by Administrator on 2016/1/25.
 */
public class TwoDimensionDialog extends DialogFragment {

    private ImageView twoDimensionDetail;
    private Bitmap bitmap;

    public TwoDimensionDialog(Bitmap bitmap) {
        super();
        this.bitmap=bitmap;
    }
    public TwoDimensionDialog(){
        super();
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.two_dimension_dialog, null);
        twoDimensionDetail= (ImageView) view.findViewById(R.id.two_dimension_detail);
        twoDimensionDetail.setImageBitmap(bitmap);
        builder.setView(view);
        return builder.create();
    }

}
