package com.example.CalorieTracker.Fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.renderscript.Sampler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.CalorieTracker.R;
import com.example.CalorieTracker.RegisterActivity;
import com.example.CalorieTracker.User;
import com.example.CalorieTracker.databinding.FragmentProfileBinding;
import com.example.CalorieTracker.firebasemanager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.scwang.smartrefresh.header.MaterialHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.scwang.smartrefresh.layout.footer.BallPulseFooter;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import static com.example.CalorieTracker.firebasemanager.mUser;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ProfileFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    //INIT ALL NEEDED ITEMS
    public static int PReqCode = 1;
    public static int REQUESTCODE = 9810;
//    private FirebaseAuth mAuth;




    private  FragmentProfileBinding b;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }



    public void updateProfileImage(Uri URL){
        Glide.with(getActivity()).load(URL).into(b.userProfile);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }




    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        b = DataBindingUtil.inflate(inflater,R.layout.fragment_profile,container,false);
        // Inflate the layout for this fragment
        return b.getRoot();
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        b.eMail.setText(firebasemanager.getManager().getmUser().getEmail());
        b.userNameEdit.setText(firebasemanager.getManager().getmUser().getDisplayName());
        b.weight.setText(String.valueOf(firebasemanager.fUser.getWeight()));
        b.ageEdit.setText(String.valueOf(firebasemanager.fUser.getAge()));
        b.heightChange.setText(String.valueOf(firebasemanager.fUser.getHeight()));
        Glide.with(getActivity()).load(firebasemanager.getManager().getmUser().getPhotoUrl()).into(b.userProfile);


        b.updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //get rid of keyboard
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getActivity().getWindow().getDecorView().getWindowToken(), 0);

            //photo will auto update so we need to take care of user information
                firebasemanager.getManager().updateUser( b.userNameEdit.getText().toString(), Double.valueOf(b.heightChange.getText().toString())
                        , Integer.valueOf(b.weight.getText().toString()), Integer.valueOf(b.ageEdit.getText().toString()), new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e("profileFragmentUpdate",e.getMessage());
                            }
                        }, new OnSuccessListener() {
                            @Override
                            public void onSuccess(Object o) {

                            }
                        });

              }
        });

        b.userProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                getActivity().startActivityForResult(galleryIntent,REQUESTCODE);
            }
        });


        //make update button appear after reaching bottom
        ScrollView scrollView = (ScrollView) getView().findViewById(R.id.scrolltest);
        RelativeLayout rlayout = (RelativeLayout) getView().findViewById(R.id.scrollRelative);

        if(rlayout.getMeasuredHeight() <= scrollView.getScrollY() +
                scrollView.getHeight()) {
            b.updateButton.setAlpha(1);
            //scroll view is at the very bottom
        }


    }






    private void showMessage(String message){
        Toast.makeText(getActivity(),message, Toast.LENGTH_SHORT).show();
    }

}
