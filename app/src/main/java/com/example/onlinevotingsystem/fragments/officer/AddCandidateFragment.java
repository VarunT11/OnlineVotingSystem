package com.example.onlinevotingsystem.fragments.officer;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.onlinevotingsystem.R;
import com.example.onlinevotingsystem.classes.Candidate;
import com.example.onlinevotingsystem.classes.User;
import com.example.onlinevotingsystem.constants.HashMapConstants;
import com.example.onlinevotingsystem.database.DatabaseUpdater;
import com.example.onlinevotingsystem.fragments.shared.ProgressIndicatorFragment;
import com.example.onlinevotingsystem.utils.DateTimeUtils;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputLayout;

import org.jetbrains.annotations.NotNull;

import java.util.Date;
import java.util.HashMap;

public class AddCandidateFragment extends Fragment implements DatabaseUpdater.DatabaseUpdateInterface {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_candidate, container, false);
    }

    TextInputLayout inputName, inputPhoneNum, inputSymbolName;
    Button btnChooseDob, btnSubmit;
    TextView tvCandidateDob;

    long dob;
    int pollNum;

    ProgressIndicatorFragment progressIndicatorFragment;

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        inputName=view.findViewById(R.id.ilAddCandidateName);
        inputPhoneNum=view.findViewById(R.id.ilAddCandidatePhoneNum);
        inputSymbolName=view.findViewById(R.id.ilAddCandidateSymbolName);
        btnChooseDob=view.findViewById(R.id.btnAddCandidateChooseDOB);
        btnSubmit=view.findViewById(R.id.btnAddCandidateSubmit);
        tvCandidateDob=view.findViewById(R.id.tvAddCandidateDOB);

        dob=new Date().getTime();
        pollNum=1;

        tvCandidateDob.setText(getDisplayTime(dob));

        btnChooseDob.setOnClickListener(v -> {

        });

        btnSubmit.setOnClickListener(v -> {
            String name, phoneNum, symbolName;
            name=inputName.getEditText().getText().toString();
            phoneNum=inputPhoneNum.getEditText().getText().toString();
            symbolName=inputSymbolName.getEditText().getText().toString();
            if(name.isEmpty() || phoneNum.isEmpty() || symbolName.isEmpty()){
                Toast.makeText(requireActivity(), "Please Fill all the Details", Toast.LENGTH_SHORT).show();
            }
            else {
                AlertDialog alertDialog=new MaterialAlertDialogBuilder(requireActivity())
                        .setTitle("Alert")
                        .setMessage("Are you sure you want to add the new User")
                        .setPositiveButton("YES", (dialog, which) -> {
                            Candidate candidate=new Candidate(name,dob,phoneNum,symbolName,pollNum);

                            HashMap<String,Object> hashMap=new HashMap<>();
                            hashMap.put(HashMapConstants.UPDATE_TYPE_KEY,HashMapConstants.UPDATE_TYPE_ADD_CANDIDATE);
                            hashMap.put(HashMapConstants.UPDATE_PARAM_CANDIDATE_KEY,candidate);

                            progressIndicatorFragment=ProgressIndicatorFragment.newInstance("Syncing with Server","Adding New Candidate");
                            progressIndicatorFragment.show(getParentFragmentManager(),"AddCandidateProgress");

                            new DatabaseUpdater(hashMap,this).execute();
                        })
                        .setNegativeButton("NO", (dialog, which) -> {
                            dialog.dismiss();
                        })
                        .create();
                alertDialog.show();
            }
        });
    }

    private String  getDisplayTime(long time){
        return DateTimeUtils.getDisplayDate(time)+" "+DateTimeUtils.getDisplayTime(time);
    }

    @Override
    public void onDataUpdated(String type, boolean result, String error) {
        if(type.equals(HashMapConstants.UPDATE_TYPE_ADD_CANDIDATE)){
            progressIndicatorFragment.dismiss();
            if(result){
                inputName.getEditText().setText("");
                inputPhoneNum.getEditText().setText("");
                inputSymbolName.getEditText().setText("");
                dob=new Date().getTime();
                Toast.makeText(requireActivity(), "Candidate Added Successfully", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(requireActivity(), "Error in Adding Candidate: "+error, Toast.LENGTH_SHORT).show();
            }
        }
    }
}