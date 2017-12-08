package ustc.sse.a4print.fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.flyco.animation.BaseAnimatorSet;
import com.flyco.animation.FadeExit.FadeExit;
import com.flyco.animation.FlipEnter.FlipVerticalSwingEnter;
import com.flyco.dialog.listener.OnBtnClickL;
import com.flyco.dialog.widget.NormalDialog;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cz.msebera.android.httpclient.Header;
import ustc.sse.a4print.R;
import ustc.sse.a4print.Tools.CustomListView;
import ustc.sse.a4print.Tools.T;
import ustc.sse.a4print.activity.MainActivity;
import ustc.sse.a4print.net.AsyncHttpCilentUtil;
import ustc.sse.a4print.net.HostIp;

/**
 * A simple {@link Fragment} subclass.
 */
public class DocumentFragment extends ListFragment {


    private DocumentAdapter mAdapter;
    private List<Map<String, Object>> mDocData;
    private List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
    private TextView noDocuments;
    private CustomListView customListView;
    private RadioButton rbtnSelectAll;
    private LinearLayout selectAllLayout;
    private TextView totalPages;
    private TextView NumberOfDocuments;
    private LinearLayout printLayout;

    public DocumentFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_document, container, false);
        mAdapter = new DocumentAdapter(getActivity());
        initView(v);
        reLoadDocuments();
        mDocData = list;
        setListAdapter(mAdapter);
        return v;
    }

    private void initView(View v) {
        noDocuments = (TextView) v.findViewById(R.id.no_document);
        rbtnSelectAll = (RadioButton) v.findViewById(R.id.document_select_all);
        selectAllLayout = (LinearLayout) v.findViewById(R.id.document_select_all_layout);
        totalPages = (TextView) v.findViewById(R.id.document_total_pages);
        NumberOfDocuments = (TextView) v.findViewById(R.id.document_number_of_documents);
        printLayout = (LinearLayout) v.findViewById(R.id.document_print_layout);
        selectAllLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (rbtnSelectAll.isChecked()) {
                    rbtnSelectAll.setChecked(false);
                    for (Map<String, Object> item : mDocData) {
                        item.put("isSelected", false);
                    }
                    mAdapter.notifyDataSetChanged();
                    totalPages.setText("0页");
                    NumberOfDocuments.setText("打印(0)");
                } else {
                    rbtnSelectAll.setChecked(true);
                    int pages = 0;
                    for (Map<String, Object> item : mDocData) {
                        item.put("isSelected", true);
                        pages += Integer.parseInt(item.get("filePages").toString());
                    }
                    mAdapter.notifyDataSetChanged();
                    totalPages.setText(pages + "页");
                    NumberOfDocuments.setText("打印(" + mDocData.size() + ")");
                }
            }
        });
        printLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
                for (Map<String, Object> item : mDocData) {
                    if ((Boolean) item.get("isSelected")) {
                        Map<String, Object> map = new HashMap<String, Object>();
                        map.put("docName", item.get("fileName"));
                        map.put("docPages", item.get("filePages"));
                        map.put("docPages_old", item.get("filePages"));
                        map.put("id", item.get("fileId"));
                        map.put("docCopies", "1");
                        map.put("docPath", "internet");
                        map.put("printType", "打印类型");

                        list.add(map);
                    }
                }
                if (list.size() > 0) {
                    PrintFragment listener;
                    MainActivity.mFragmentTabHost.doTabChanged("打印", getFragmentManager().beginTransaction()).commit();
                    getActivity().getSupportFragmentManager().executePendingTransactions();
                    listener = (PrintFragment) getActivity().getSupportFragmentManager().findFragmentByTag("打印");
                    PrintFragment.mDocData = list;
                    PrintFragment.myDocAdapter.notifyDataSetChanged();
                    listener.reSetListHeight();
                    MainActivity.mFragmentTabHost.setCurrentTabByTag("打印");
                } else {
                    T.showShort(getActivity(), "未选中文件！");
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        customListView = (CustomListView) getListView();
        customListView.setonRefreshListener(new CustomListView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                reLoadDocuments();
            }
        });
    }

    public final class ViewHolder {
        public TextView fileName;
        public TextView filePages;
        public TextView filePrice;
        public ImageView more;
        public RadioButton fileSelect;
        public ImageView fileImage;
        public LinearLayout itemLayout;
        public LinearLayout moreLayout;
    }

    class DocumentAdapter extends BaseAdapter {

        private LayoutInflater mInflater;


        public DocumentAdapter(Context context) {
            this.mInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return mDocData.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {

                holder = new ViewHolder();

                convertView = mInflater.inflate(R.layout.item_documents, null);
                holder.fileSelect = (RadioButton) convertView.findViewById(R.id.document_radio_select);
                holder.fileName = (TextView) convertView.findViewById(R.id.document_tv_fileName);
                holder.filePages = (TextView) convertView.findViewById(R.id.document_tv_filePages);
                holder.filePrice = (TextView) convertView.findViewById(R.id.document_filePrice);
                holder.more = (ImageView) convertView.findViewById(R.id.document_iv_more);
                holder.fileImage = (ImageView) convertView.findViewById(R.id.document_iv_fileImage);
                holder.itemLayout = (LinearLayout) convertView.findViewById(R.id.document_item_layout);
                holder.moreLayout = (LinearLayout) convertView.findViewById(R.id.document_more_layout);
                convertView.setTag(holder);

            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.fileName.setText(mDocData.get(position).get("fileName").toString());
            holder.filePages.setText(mDocData.get(position).get("filePages").toString() + "页");
            holder.filePrice.setText(mDocData.get(position).get("fileSize").toString());
            String fileTypeStr = mDocData.get(position).get("fileType").toString();
            if (fileTypeStr.equals("doc")) {
                holder.fileImage.setImageResource(R.drawable.doc);
            } else if (fileTypeStr.equals("docx")) {
                holder.fileImage.setImageResource(R.drawable.docx);
            } else if (fileTypeStr.equals("pdf")) {
                holder.fileImage.setImageResource(R.drawable.pdf);
            } else if (fileTypeStr.equals("jpeg")) {
                holder.fileImage.setImageResource(R.drawable.jpeg);
            } else if (fileTypeStr.equals("jpg")) {
                holder.fileImage.setImageResource(R.drawable.jpg);
            } else if (fileTypeStr.equals("png")) {
                holder.fileImage.setImageResource(R.drawable.png);
            } else if (fileTypeStr.equals("ppt")) {
                holder.fileImage.setImageResource(R.drawable.ppt);
            } else if (fileTypeStr.equals("pptx")) {
                holder.fileImage.setImageResource(R.drawable.pptx);
            } else {
                holder.fileImage.setImageResource(R.drawable.file);
            }
            holder.fileSelect.setChecked((Boolean) mDocData.get(position).get("isSelected"));
            holder.itemLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if ((Boolean) mDocData.get(position).get("isSelected")) {
                        mDocData.get(position).put("isSelected", false);
                    } else {
                        mDocData.get(position).put("isSelected", true);
                    }
                    mAdapter.notifyDataSetChanged();
                    updateTotalInfo();
                }
            });
            holder.moreLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deletedDocDialog(mDocData.get(position).get("fileId").toString());
                }
            });
            return convertView;
        }
    }

    private void updateTotalInfo() {
        int pages = 0;
        int numOfDoc = 0;
        for (Map<String, Object> item : mDocData) {
            if ((Boolean) item.get("isSelected")) {
                pages += Integer.parseInt(item.get("filePages").toString());
                numOfDoc++;
            }
        }
        totalPages.setText(pages + "页");
        NumberOfDocuments.setText("打印(" + numOfDoc + ")");
    }

    private void reLoadDocuments() {
        list.clear();
        AsyncHttpClient client = AsyncHttpCilentUtil.getInstance(getActivity());
        client.post("http://" + HostIp.ip + "/A4print/getUserAllFiles.do", null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                //super.onSuccess(statusCode, headers, response);
                JSONObject object = response;
                //JSONObject message=object.getJSONObject("message");
                JSONArray result = null;
                try {
                    result = object.getJSONArray("result");
                    if (result.length() == 0) {
                        noDocuments.setText("没有文档！");
                        mDocData = list;
                        setListAdapter(mAdapter);
                        mAdapter.notifyDataSetChanged();
                        customListView.onRefreshComplete();
                    } else {
                        noDocuments.setText("");
                        for (int i = 0; i < result.length(); i++) {
                            JSONObject docment1 = result.getJSONObject(i);
                            String fileName = docment1.getString("fileName");
                            String filePages = docment1.getString("filePages");
                            String fileType = docment1.getString("fileType");
                            String fileId = docment1.getString("id");
                            String fileSize = docment1.getString("fileSize");

                            Map<String, Object> map0 = new HashMap<String, Object>();
                            map0.put("fileName", fileName);
                            map0.put("filePages", filePages);
                            map0.put("fileType", fileType);
                            map0.put("fileId", fileId);
                            map0.put("fileSize", fileSize);
                            map0.put("isSelected", false);

                            //计算每个文件的价格，单价后期需要改成动态值
                            int pages = Integer.parseInt(filePages);
                            double price = pages * 0.1;
                            map0.put("filePrice", price + "");

                            list.add(map0);
                            mDocData = list;
                            setListAdapter(mAdapter);
                            mAdapter.notifyDataSetChanged();

                            customListView.onRefreshComplete();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                System.out.println("error" + responseString);
            }
        });
    }

    private void deletedDocDialog(final String fileId) {
        final NormalDialog dialog = new NormalDialog(getActivity());
        BaseAnimatorSet bas_in = new FlipVerticalSwingEnter();
        BaseAnimatorSet bas_out = new FadeExit();

        dialog.content("你真的要删除这个文件吗?")//
                .showAnim(bas_in)//
                .dismissAnim(bas_out)//
                .show();
        dialog.setOnBtnClickL(new OnBtnClickL() {
            @Override
            public void onBtnClick() {
                dialog.dismiss();

            }
        }, new OnBtnClickL() {
            @Override
            public void onBtnClick() {

                deleteDocById(fileId);
                dialog.dismiss();
            }
        });
    }

    private void deleteDocById(String fileId) {
        RequestParams params = new RequestParams();
        params.put("fileId", fileId);
        AsyncHttpClient client = AsyncHttpCilentUtil.getInstance(getActivity());
        client.post("http://" + HostIp.ip + "/A4print/removeFile.do", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                JSONObject object = response;
                try {
                    boolean mes = object.getBoolean("success");
                    if (mes) {
                        T.showShort(getActivity(), "删除成功");
                        reLoadDocuments();
                    } else {
                        T.showShort(getActivity(), "订单中包含该文件，不能删除！");
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                System.out.println("error" + responseString);
            }
        });
    }

    public interface DocumentPageToPrintListener {
        void documentPageToPrint(int count);

        void reSetListHeight();
    }
}
