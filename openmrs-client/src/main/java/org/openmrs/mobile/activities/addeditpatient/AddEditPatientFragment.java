/*
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */

package org.openmrs.mobile.activities.addeditpatient;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.ExifInterface;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.StringDef;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;

import com.openmrs.android_sdk.library.OpenMRSLogger;
import com.openmrs.android_sdk.library.models.ConceptAnswers;
import com.openmrs.android_sdk.library.models.Patient;
import com.openmrs.android_sdk.library.models.PersonAddress;
import com.openmrs.android_sdk.library.models.PersonName;
import com.openmrs.android_sdk.library.models.Resource;
import com.openmrs.android_sdk.utilities.ApplicationConstants;
import com.openmrs.android_sdk.utilities.DateUtils;
import com.openmrs.android_sdk.utilities.ImageUtils;
import com.openmrs.android_sdk.utilities.StringUtils;
import com.openmrs.android_sdk.utilities.ToastUtil;
import com.google.android.gms.common.api.ApiException;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.material.snackbar.Snackbar;
import com.yalantis.ucrop.UCrop;

import org.jetbrains.annotations.NotNull;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.ACBaseFragment;
import org.openmrs.mobile.activities.dialog.CustomDialogModel;
import org.openmrs.mobile.activities.dialog.CustomFragmentDialog;
import org.openmrs.mobile.activities.dialog.CustomPickerDialog;
import org.openmrs.mobile.activities.patientdashboard.PatientDashboardActivity;
import org.openmrs.mobile.activities.patientdashboard.details.PatientPhotoActivity;
import org.openmrs.mobile.bundle.CustomDialogBundle;
import org.openmrs.mobile.databinding.FragmentPatientInfoBinding;
import org.openmrs.mobile.listeners.watcher.PatientBirthdateValidatorWatcher;
import org.openmrs.mobile.utilities.ViewUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.annotation.Retention;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

import static java.lang.annotation.RetentionPolicy.SOURCE;

@RuntimePermissions
public class AddEditPatientFragment extends ACBaseFragment<AddEditPatientContract.Presenter> implements AddEditPatientContract.View, CustomPickerDialog.onInputSelected {
    AlertDialog alertDialog;
    private FragmentPatientInfoBinding binding;
    private LocalDate birthDate;
    private DateTime bdt;
    private Boolean isPatientUnidentified = false;
    private DateTimeFormatter dateTimeFormatter;
    private ArrayList<String> cityList = new ArrayList<>();
    private PlacesClient placesClient;
    private Bitmap patientPhoto = null;
    private String patientName;
    private File output = null;
    private OpenMRSLogger logger = new OpenMRSLogger();
    private boolean isUpdatePatient = false;
    private Patient updatedPatient;
    private String causeOfDeathUUID = "";
    private Resource causeOfDeath;
    private List<CustomDialogModel> dialogList = new ArrayList<>();

    public static AddEditPatientFragment newInstance() {
        return new AddEditPatientFragment();
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentPatientInfoBinding.inflate(inflater, container, false);
        setHasOptionsMenu(true);
        addListeners();
        initializePlaces(mPresenter.getPlaces());
        fillFields(mPresenter.getPatientToUpdate());
        return binding.getRoot();
    }

    private void initializePlaces(PlacesClient places) {
        placesClient = places;
    }

    @Override
    public void finishPatientInfoActivity() {
        requireActivity().finish();
    }

    @Override
    public void setErrorsVisibility(boolean givenNameError,
                                    boolean familyNameError,
                                    boolean dayOfBirthError,
                                    boolean addressError,
                                    boolean countryError,
                                    boolean genderError,
                                    boolean countryNull,
                                    boolean stateError,
                                    boolean cityError,
                                    boolean postalError) {
        // Only two dedicated text views will be visible for error messages.
        // Rest error messages will be displayed in dedicated TextInputLayouts.
        if (dayOfBirthError) {
            binding.dobError.setVisibility(View.VISIBLE);

            dateTimeFormatter = DateTimeFormat.forPattern(DateUtils.DEFAULT_DATE_FORMAT);
            String minimumDate = DateTime.now().minusYears(
                ApplicationConstants.RegisterPatientRequirements.MAX_PATIENT_AGE)
                .toString(dateTimeFormatter);
            String maximumDate = DateTime.now().toString(dateTimeFormatter);
            if (binding.unidentifiedCheckbox.isChecked()) {
                binding.dobError.setText(getString(R.string.dob_error_for_unidentified));
            } else {
                binding.dobError.setText(getString(R.string.dob_error, minimumDate, maximumDate));
            }
        } else {
            binding.dobError.setVisibility(View.GONE);
        }

        if (genderError) {
            this.binding.gendererror.setVisibility(View.VISIBLE);
        } else {
            this.binding.gendererror.setVisibility(View.GONE);
        }

        if(addressError) {
            this.binding.addressError.setVisibility(View.VISIBLE);
        } else {
            this.binding.addressError.setVisibility(View.GONE);
        }
    }

    @Override
    public void scrollToTop() {
        binding.scrollView.smoothScrollTo(0, binding.scrollView.getPaddingTop());
    }

    private Patient updatePatientWithData(Patient patient) {
        String emptyError = getString(R.string.emptyerror);

        // errors for the empty fields must be filtered
        if (binding.unidentifiedCheckbox.isChecked()) {

            PersonName name = new PersonName();
            name.setFamilyName(getString(R.string.unidentified_patient_name));
            name.setGivenName(getString(R.string.unidentified_patient_name));
            List<PersonName> names = new ArrayList<>();
            names.add(name);
            patient.setNames(names);

            List<PersonAddress> addresses = new ArrayList<>();
            patient.setAddresses(addresses);
        } else {
            // Validate address
            if (ViewUtils.isEmpty(binding.addressOne)
                && ViewUtils.isEmpty(binding.addressTwo)
                && ViewUtils.isEmpty(binding.cityAutoComplete)
                && ViewUtils.isEmpty(binding.postalCode)
                && ViewUtils.isCountryCodePickerEmpty(binding.countryCodeSpinner)
                && ViewUtils.isEmpty(binding.stateAutoComplete)) {

                binding.addressError.setText(R.string.atleastone);
                binding.textInputLayoutAddress.setErrorEnabled(true);
                binding.textInputLayoutAddress.setError(getString(R.string.atleastone));
            } else if (!ViewUtils.validateText(ViewUtils.getInput(binding.addressOne), ViewUtils.ILLEGAL_ADDRESS_CHARACTERS)
                || !ViewUtils.validateText(ViewUtils.getInput(binding.addressTwo), ViewUtils.ILLEGAL_ADDRESS_CHARACTERS)) {

                binding.addressError.setText(getString(R.string.addr_invalid_error));
                binding.textInputLayoutAddress.setErrorEnabled(true);
                binding.textInputLayoutAddress.setError(getString(R.string.addr_invalid_error));
            } else {
                binding.textInputLayoutAddress.setErrorEnabled(false);
            }

            // Add address
            PersonAddress address = new PersonAddress();
            address.setAddress1(ViewUtils.getInput(binding.addressOne));
            address.setAddress2(ViewUtils.getInput(binding.addressTwo));
            address.setCityVillage(ViewUtils.getInput(binding.cityAutoComplete));
            address.setPostalCode(ViewUtils.getInput(binding.postalCode));
            address.setCountry(binding.countryCodeSpinner.getSelectedCountryName());
            address.setStateProvince(ViewUtils.getInput(binding.stateAutoComplete));
            address.setPreferred(true);

            List<PersonAddress> addresses = new ArrayList<>();
            addresses.add(address);
            patient.setAddresses(addresses);

            // Invalid characters for given name only
            String givenNameError = getString(R.string.fname_invalid_error);
            // Invalid characters for the middle name
            String middleNameError = getString(R.string.midname_invalid_error);
            // Invalid family name
            String familyNameError = getString(R.string.lname_invalid_error);

            // First name validation
            if (ViewUtils.isEmpty(binding.firstName)) {
                binding.textInputLayoutFirstName.setErrorEnabled(true);
                binding.textInputLayoutFirstName.setError(emptyError);
            } else if (!ViewUtils.validateText(ViewUtils.getInput(binding.firstName), ViewUtils.ILLEGAL_CHARACTERS)) {
                binding.textInputLayoutFirstName.setErrorEnabled(true);
                binding.textInputLayoutFirstName.setError(givenNameError);
            } else {
                binding.textInputLayoutFirstName.setErrorEnabled(false);
            }

            // Middle name validation (can be empty)
            if (!ViewUtils.validateText(ViewUtils.getInput(binding.middlename), ViewUtils.ILLEGAL_CHARACTERS)) {
                binding.textInputLayoutMiddlename.setErrorEnabled(true);
                binding.textInputLayoutMiddlename.setError(middleNameError);
            } else {
                binding.textInputLayoutMiddlename.setErrorEnabled(false);
            }

            // Family name validation
            if (ViewUtils.isEmpty(binding.surname)) {
                binding.textInputLayoutSurname.setErrorEnabled(true);
                binding.textInputLayoutSurname.setError(emptyError);
            } else if (!ViewUtils.validateText(ViewUtils.getInput(binding.surname), ViewUtils.ILLEGAL_CHARACTERS)) {
                binding.textInputLayoutSurname.setErrorEnabled(true);
                binding.textInputLayoutSurname.setError(familyNameError);
            } else {
                binding.textInputLayoutSurname.setErrorEnabled(false);
            }

            // Add names
            PersonName name = new PersonName();
            name.setFamilyName(ViewUtils.getInput(binding.surname));
            name.setGivenName(ViewUtils.getInput(binding.firstName));
            name.setMiddleName(ViewUtils.getInput(binding.middlename));

            List<PersonName> names = new ArrayList<>();
            names.add(name);
            patient.setNames(names);
        }
        // Add birthdate
        String birthdate = null;
        if (ViewUtils.isEmpty(binding.dobEditText)) {
            if (!StringUtils.isBlank(ViewUtils.getInput(binding.estimatedYear)) || !StringUtils.isBlank(ViewUtils.getInput(binding.estimatedMonth))) {
                dateTimeFormatter = DateTimeFormat.forPattern(DateUtils.OPEN_MRS_REQUEST_PATIENT_FORMAT);

                int yeardiff = ViewUtils.isEmpty(binding.estimatedYear) ? 0 : Integer.parseInt(binding.estimatedYear.getText().toString());
                int mondiff = ViewUtils.isEmpty(binding.estimatedMonth) ? 0 : Integer.parseInt(binding.estimatedMonth.getText().toString());
                LocalDate now = new LocalDate();
                bdt = now.toDateTimeAtStartOfDay().toDateTime();
                bdt = bdt.minusYears(yeardiff);
                bdt = bdt.minusMonths(mondiff);
                patient.setBirthdateEstimated(true);
                birthdate = dateTimeFormatter.print(bdt);
            }
        } else {
            String unvalidatedDate = binding.dobEditText.getText().toString().trim();

            DateTime minDateOfBirth = DateTime.now().minusYears(
                ApplicationConstants.RegisterPatientRequirements.MAX_PATIENT_AGE);
            DateTime maxDateOfBirth = DateTime.now();

            if (DateUtils.validateDate(unvalidatedDate, minDateOfBirth, maxDateOfBirth)) {
                dateTimeFormatter = DateTimeFormat.forPattern(DateUtils.DEFAULT_DATE_FORMAT);
                bdt = dateTimeFormatter.parseDateTime(unvalidatedDate);

                dateTimeFormatter = DateTimeFormat.forPattern(DateUtils.OPEN_MRS_REQUEST_PATIENT_FORMAT);
                birthdate = dateTimeFormatter.print(bdt);
            }
        }
        patient.setBirthdate(birthdate);

        // Validating gender
        String[] genderChoices = {StringValue.MALE, StringValue.FEMALE};
        int index = binding.gender.indexOfChild(getActivity().findViewById(binding.gender.getCheckedRadioButtonId()));
        if (index != -1) {
            patient.setGender(genderChoices[index]);
        } else {
            patient.setGender(null);
        }

        // Add patient photo
        if (patientPhoto != null) {
            patient.setPhoto(patientPhoto);
        }

        if (binding.deceasedCheckbox.isChecked() && !causeOfDeathUUID.isEmpty()) {
            patient.setDeceased(true);
            patient.setCauseOfDeath(causeOfDeath);
        } else {
            patient.setDeceased(false);
            patient.setCauseOfDeath(new Resource());
        }
        return patient;
    }

    private Patient createPatient() {
        Patient patient = new Patient();
        updatePatientWithData(patient);
        patient.setUuid(" ");
        return patient;
    }

    private Patient updatePatient(Patient patient) {
        return updatePatientWithData(patient);
    }

    @Override
    public void hideSoftKeys() {
        View view = this.getActivity().getCurrentFocus();
        if (view == null) {
            view = new View(this.getActivity());
        }
        InputMethodManager inputMethodManager = (InputMethodManager) this.getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    public void setProgressBarVisibility(boolean visibility) {
        binding.progressBar.setVisibility(visibility ? View.VISIBLE : View.GONE);
        if(binding.progressBar.getVisibility()==View.VISIBLE) {
            getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            binding.transpScreenScreen.setVisibility(View.VISIBLE);
        }
        else {
            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            binding.transpScreenScreen.setVisibility(View.GONE);
        }
    }

    @Override
    public void showSimilarPatientDialog(List<Patient> patients, Patient newPatient) {
        setProgressBarVisibility(false);
        CustomDialogBundle similarPatientsDialog = new CustomDialogBundle();
        similarPatientsDialog.setTitleViewMessage(getString(R.string.similar_patients_dialog_title));
        similarPatientsDialog.setRightButtonText(getString(R.string.dialog_button_register_new));
        similarPatientsDialog.setRightButtonAction(CustomFragmentDialog.OnClickAction.REGISTER_PATIENT);
        similarPatientsDialog.setLeftButtonText(getString(R.string.dialog_button_cancel));
        similarPatientsDialog.setLeftButtonAction(CustomFragmentDialog.OnClickAction.DISMISS);
        similarPatientsDialog.setPatientsList(patients);
        similarPatientsDialog.setNewPatient(newPatient);
        ((AddEditPatientActivity) this.getActivity()).createAndShowDialog(similarPatientsDialog, ApplicationConstants.DialogTAG.SIMILAR_PATIENTS_TAG);
    }

    @Override
    public void startPatientDashbordActivity(Patient patient) {
        Intent intent = new Intent(getActivity(), PatientDashboardActivity.class);
        intent.putExtra(ApplicationConstants.BundleKeys.PATIENT_ID_BUNDLE, patient.getId());
        startActivity(intent);
    }

    @Override
    public void showUpgradeRegistrationModuleInfo() {
        ToastUtil.notifyLong(getResources().getString(R.string.registration_core_info));
    }

    @Override
    public boolean areFieldsNotEmpty() {
        return (!ViewUtils.isEmpty(binding.firstName) ||
            (!ViewUtils.isEmpty(binding.middlename)) ||
            (!ViewUtils.isEmpty(binding.surname)) ||
            (!ViewUtils.isEmpty(binding.dobEditText)) ||
            (!ViewUtils.isEmpty(binding.estimatedYear)) ||
            (!ViewUtils.isEmpty(binding.addressOne)) ||
            (!ViewUtils.isEmpty(binding.addressTwo)) ||
            (!ViewUtils.isEmpty(binding.cityAutoComplete)) ||
            (!ViewUtils.isEmpty(binding.stateAutoComplete)) ||
            (!ViewUtils.isCountryCodePickerEmpty(binding.countryCodeSpinner)) ||
            (!ViewUtils.isEmpty(binding.postalCode)));
    }

    @Override
    public void cannotMarkDeceased(String message) {
        binding.deceasedProgressBar.setVisibility(View.GONE);
        binding.deceasedSpinner.setVisibility(View.GONE);
        binding.deceasedCheckbox.setChecked(false);
        if (message.isEmpty()) {
            ToastUtil.error(getString(R.string.no_death_concepts_in_server));
        } else {
            ToastUtil.error(message);
        }
    }

    @Override
    public void cannotMarkDeceased(int messageID) {
        binding.deceasedProgressBar.setVisibility(View.GONE);
        binding.deceasedSpinner.setVisibility(View.GONE);
        binding.deceasedCheckbox.setChecked(false);
        ToastUtil.error(getString(messageID));
    }

    @Override
    public void updateCauseOfDeathSpinner(ConceptAnswers concept) {
        binding.deceasedProgressBar.setVisibility(View.GONE);
        binding.deceasedSpinner.setVisibility(View.VISIBLE);
        List<Resource> answers = concept.getAnswers();
        String[] answerDisplays = new String[answers.size()];
        for (int i = 0; i < answers.size(); i++) {
            answerDisplays[i] = answers.get(i).getDisplay();
        }
        ArrayAdapter<String> adapterAnswers = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, answerDisplays);
        binding.deceasedSpinner.setAdapter(adapterAnswers);
        binding.deceasedSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
                String display = binding.deceasedSpinner.getSelectedItem().toString();
                for (int i = 0; i < answers.size(); i++) {
                    if (display.equals(answers.get(i).getDisplay())) {
                        causeOfDeathUUID = answers.get(i).getUuid();
                        causeOfDeath = answers.get(i);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void fillFields(final Patient patient) {
        if (patient != null) {
            //no need for un-identification option once the patient is registered
            binding.unidentifiedCheckbox.setVisibility(View.GONE);
            // show deceased option only when patient is registered
            binding.deceasedCardview.setVisibility(View.VISIBLE);

            //Change to Update Patient Form
            String updatePatientStr = getResources().getString(R.string.action_update_patient_data);
            this.getActivity().setTitle(updatePatientStr);

            isUpdatePatient = true;
            updatedPatient = patient;

            binding.firstName.setText(patient.getName().getGivenName());
            binding.middlename.setText(patient.getName().getMiddleName());
            binding.surname.setText(patient.getName().getFamilyName());

            patientName = patient.getName().getNameString();

            if (StringUtils.notNull(patient.getBirthdate()) || StringUtils.notEmpty(patient.getBirthdate())) {
                bdt = DateUtils.convertTimeString(patient.getBirthdate());
                binding.dobEditText.setText(DateUtils.convertTime(DateUtils.convertTime(bdt.toString(), DateUtils.OPEN_MRS_REQUEST_FORMAT),
                    DateUtils.DEFAULT_DATE_FORMAT));
            }

            if ((StringValue.MALE).equals(patient.getGender())) {
                binding.gender.check(R.id.male);
            } else if ((StringValue.FEMALE).equals(patient.getGender())) {
                binding.gender.check(R.id.female);
            }

            binding.addressOne.setText(patient.getAddress().getAddress1());
            binding.addressTwo.setText(patient.getAddress().getAddress2());
            binding.cityAutoComplete.setText(patient.getAddress().getCityVillage());
            binding.stateAutoComplete.setText(patient.getAddress().getStateProvince());
            binding.postalCode.setText(patient.getAddress().getPostalCode());

            if (patient.getPhoto() != null) {
                patientPhoto = patient.getPhoto();
                Bitmap resizedPatientPhoto = patient.getResizedPhoto();
                binding.patientPhoto.setImageBitmap(resizedPatientPhoto);
            }

            if (patient.isDeceased()) {
                binding.deceasedCheckbox.setChecked(true);
            }
        }
    }

    private void addSuggestionsToCities() {
        String country_name = binding.countryCodeSpinner.getSelectedCountryName();
        country_name = country_name.replace("(", "");
        country_name = country_name.replace(")", "");
        country_name = country_name.replace(" ", "");
        country_name = country_name.replace("-", "_");
        country_name = country_name.replace(".", "");
        country_name = country_name.replace("'", "");
        int resourceId = this.getResources().getIdentifier(country_name.toLowerCase(), "array", getContext().getPackageName());
        if (resourceId != 0) {
            String[] states = getContext().getResources().getStringArray(resourceId);
            ArrayAdapter<String> state_adapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_dropdown_item_1line, states);
            binding.stateAutoComplete.setAdapter(state_adapter);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        AddEditPatientFragmentPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    private void addListeners() {
        binding.gender.setOnCheckedChangeListener((radioGroup, checkedId) -> binding.gendererror.setVisibility(View.GONE));
        binding.stateAutoComplete.setOnFocusChangeListener((view, hasFocus) -> addSuggestionsToCities());

        binding.dobEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Only needs afterTextChanged method from TextWacher
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Only needs afterTextChanged method from TextWacher
                //add slash after entering date and month
                String str=binding.dobEditText.getText().toString();
                int textLength=binding.dobEditText.getText().length();
                if (textLength == 3) {
                    if (!str.contains("/")) {
                        binding.dobEditText.setText(new StringBuilder(binding.dobEditText.getText().toString()).insert(str.length() - 1, "/").toString());
                        binding.dobEditText.setSelection(binding.dobEditText.getText().length());
                    }
                }
                if (textLength == 6) {
                    if (!str.substring(3).contains("/")) {
                        binding.dobEditText.setText(new StringBuilder(binding.dobEditText.getText().toString()).insert(str.length() - 1, "/").toString());
                        binding.dobEditText.setSelection(binding.dobEditText.getText().length());
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                // If a considerable amount of text is filled in edDob, then remove 'Estimated age' fields.
                if (s.length() >= 8) {
                    binding.estimatedMonth.getText().clear();
                    binding.estimatedYear.getText().clear();
                }
            }
        });

        binding.datePicker.setOnClickListener(v -> {
            int cYear;
            int cMonth;
            int cDay;

            if (bdt == null) {
                Calendar currentDate = Calendar.getInstance();
                cYear = currentDate.get(Calendar.YEAR);
                cMonth = currentDate.get(Calendar.MONTH);
                cDay = currentDate.get(Calendar.DAY_OF_MONTH);
            } else {
                cYear = bdt.getYear();
                cMonth = bdt.getMonthOfYear() - 1;
                cDay = bdt.getDayOfMonth();
            }

            binding.estimatedMonth.getText().clear();
            binding.estimatedYear.getText().clear();

            DatePickerDialog mDatePicker = new DatePickerDialog(AddEditPatientFragment.this.getActivity(), (datePicker, selectedYear, selectedMonth, selectedDay) -> {
                int adjustedMonth = selectedMonth + 1;
                binding.dobEditText.setText(String.format("%02d",selectedDay) + "/" + String.format("%02d",adjustedMonth) + "/" + selectedYear);
                birthDate = new LocalDate(selectedYear, adjustedMonth, selectedDay);
                bdt = birthDate.toDateTimeAtStartOfDay().toDateTime();
            }, cYear, cMonth, cDay);
            mDatePicker.getDatePicker().setMaxDate(System.currentTimeMillis());
            mDatePicker.setTitle(getString(R.string.date_picker_title));
            mDatePicker.show();
        });

        binding.capturePhoto.setOnClickListener(view -> {
            dialogList.clear();
            dialogList.add(new CustomDialogModel(getString(R.string.dialog_take_photo), R.drawable.ic_photo_camera));
            dialogList.add(new CustomDialogModel(getString(R.string.dialog_choose_photo), R.drawable.ic_photo_library));
            if (patientPhoto != null) {
                dialogList.add(new CustomDialogModel(getString(R.string.dialog_remove_photo), R.drawable.ic_photo_delete));
            }
            CustomPickerDialog customPickerDialog = new CustomPickerDialog(dialogList);
            customPickerDialog.setTargetFragment(AddEditPatientFragment.this, 1000);
            customPickerDialog.show(getFragmentManager(), "tag");
        });

        binding.patientPhoto.setOnClickListener(view -> {
            if (output != null) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setDataAndType(Uri.fromFile(output), ApplicationConstants.IMAGE_JPEG);
                startActivity(i);
            } else if (patientPhoto != null) {
                Intent intent = new Intent(getContext(), PatientPhotoActivity.class);
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                patientPhoto.compress(Bitmap.CompressFormat.PNG, 0, byteArrayOutputStream);
                intent.putExtra(ApplicationConstants.INTENT_KEY_PHOTO, byteArrayOutputStream.toByteArray());
                intent.putExtra(ApplicationConstants.INTENT_KEY_NAME, patientName);
                startActivity(intent);
            }
        });

        TextWatcher textWatcher = new PatientBirthdateValidatorWatcher(binding.dobEditText, binding.estimatedMonth, binding.estimatedYear);
        binding.estimatedMonth.addTextChangedListener(textWatcher);
        binding.estimatedYear.addTextChangedListener(textWatcher);

        //check for cities available on searching
        binding.cityAutoComplete.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                binding.cityProgressBar.setVisibility(View.VISIBLE);
                cityList.clear();

                AutocompleteSessionToken token = AutocompleteSessionToken.newInstance();

                FindAutocompletePredictionsRequest request = FindAutocompletePredictionsRequest.builder()
                    .setCountry(binding.countryCodeSpinner.getSelectedCountryNameCode().toLowerCase())
                    .setTypeFilter(TypeFilter.CITIES)
                    .setSessionToken(token)
                    .setQuery(binding.cityAutoComplete.getText().toString())
                    .build();

                placesClient.findAutocompletePredictions(request).addOnSuccessListener(response -> {
                    binding.cityProgressBar.setVisibility(View.GONE);
                    for (AutocompletePrediction autocompletePrediction : response.getAutocompletePredictions())
                        cityList.add(autocompletePrediction.getFullText(null).toString());

                    //creating an array from ArrayList to create adapter
                    String[] address = new String[cityList.size()];
                    for (int in = 0; in < cityList.size(); in++)
                        address[in] = cityList.get(in);
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.select_dialog_item, address);

                    binding.cityAutoComplete.setAdapter(adapter);

                    binding.cityAutoComplete.setOnItemClickListener((parent, view, position, id) -> {
                        String primary_text = response.getAutocompletePredictions().get(position).getPrimaryText(null).toString();
                        String secondary_text = response.getAutocompletePredictions().get(position).getSecondaryText(null).toString();

                        binding.cityAutoComplete.setText(primary_text);

                        /**
                         * if it is a city , then format received will be :
                         *      CITY, STATE, COUNTRY
                         * else it is a union territory, then it will show :
                         *      CITY, COUNTRY
                         */

                        if (secondary_text.contains(",")) {
                            int index = secondary_text.indexOf(',');
                            String state = secondary_text.substring(0, index);
                            binding.stateAutoComplete.setText(state);
                        } else {
                            binding.stateAutoComplete.setText(primary_text);
                        }
                    });
                }).addOnFailureListener((exception) -> {
                    if (exception instanceof ApiException) {
                        ApiException apiException = (ApiException) exception;
                        Log.i("Place API", "Place not found: " + apiException.getStatusCode());
                    }
                    binding.cityProgressBar.setVisibility(View.GONE);
                });
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        binding.unidentifiedCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (binding.unidentifiedCheckbox.isChecked()) {
                binding.linearLayoutName.setVisibility(View.GONE);
                binding.constraintLayoutDOB.setVisibility(View.GONE);
                binding.linearLayoutContactInfo.setVisibility(View.GONE);
                isPatientUnidentified = true;
            } else {
                binding.linearLayoutName.setVisibility(View.VISIBLE);
                binding.constraintLayoutDOB.setVisibility(View.VISIBLE);
                binding.linearLayoutContactInfo.setVisibility(View.VISIBLE);
                isPatientUnidentified = false;
            }
        });

        binding.deceasedCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (binding.deceasedCheckbox.isChecked()) {
                    binding.deceasedProgressBar.setVisibility(View.VISIBLE);
                    binding.deceasedSpinner.setVisibility(View.GONE);
                    mPresenter.getCauseOfDeathGlobalID();
                } else {
                    binding.deceasedProgressBar.setVisibility(View.GONE);
                    binding.deceasedSpinner.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    public void performFunction(int position) {
        if (position == 0) {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
            AddEditPatientFragmentPermissionsDispatcher.capturePhotoWithPermissionCheck(AddEditPatientFragment.this);
        } else if (position == 1) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, ApplicationConstants.RequestCodes.GALLERY_IMAGE_REQUEST);
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                return;
            }

            Intent i;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                i = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            } else {
                i = new Intent(Intent.ACTION_GET_CONTENT);
            }
            i.addCategory(Intent.CATEGORY_OPENABLE);
            i.setType("image/*");
            startActivityForResult(i, ApplicationConstants.RequestCodes.GALLERY_IMAGE_REQUEST);
        } else {
            binding.patientPhoto.setImageResource(R.drawable.ic_person_grey_500_48dp);
            binding.patientPhoto.invalidate();
            patientPhoto = null;
        }
    }

    @NeedsPermission({Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    public void capturePhoto() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getContext().getPackageManager()) != null) {
            File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
            output = new File(dir, getUniqueImageFileName());
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(output));
            startActivityForResult(takePictureIntent, ApplicationConstants.RequestCodes.IMAGE_REQUEST);
        }
    }

    @OnShowRationale({Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    public void showRationaleForCamera(final PermissionRequest request) {
        new AlertDialog.Builder(getActivity())
            .setMessage(R.string.permission_camera_rationale)
            .setPositiveButton(R.string.button_allow, (dialog, which) -> request.proceed())
            .setNegativeButton(R.string.button_deny, (dialog, button) -> request.cancel())
            .show();
    }

    @OnPermissionDenied({Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    public void showDeniedForCamera() {
        createSnackbarLong(R.string.permission_camera_denied)
            .show();
    }

    @OnNeverAskAgain({Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    public void showNeverAskForCamera() {
        createSnackbarLong(R.string.permission_camera_neverask)
            .show();
    }

    private Snackbar createSnackbarLong(int stringId) {
        Snackbar snackbar = Snackbar.make(binding.addEditConstraintLayout, stringId, Snackbar.LENGTH_LONG);
        View sbView = snackbar.getView();
        TextView textView = sbView.findViewById(com.google.android.material.R.id.snackbar_text);
        textView.setTextColor(Color.WHITE);
        return snackbar;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ApplicationConstants.RequestCodes.IMAGE_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                Uri sourceUri = Uri.fromFile(output);
                openCropActivity(sourceUri, sourceUri);
            } else {
                output = null;
            }
        } else if (requestCode == ApplicationConstants.RequestCodes.GALLERY_IMAGE_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                Uri sourceUri = data.getData();
                File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
                output = new File(dir, getUniqueImageFileName());
                Uri destinationUri = Uri.fromFile(output);
                openCropActivity(sourceUri, destinationUri);
            } else {
                output = null;
            }
        } else if (requestCode == UCrop.REQUEST_CROP) {
            if (resultCode == Activity.RESULT_OK) {
                patientPhoto = getResizedPortraitImage(output.getPath());
                Bitmap bitmap = ThumbnailUtils.extractThumbnail(patientPhoto, binding.patientPhoto.getWidth(), binding.patientPhoto.getHeight());
                binding.patientPhoto.setImageBitmap(bitmap);
                binding.patientPhoto.invalidate();
            } else {
                output = null;
            }
        } else if (requestCode == UCrop.RESULT_ERROR) {
            ToastUtil.error(String.valueOf(UCrop.getError(data)));
        }
    }

    private void openCropActivity(Uri sourceUri, Uri destinationUri) {
        UCrop.of(sourceUri, destinationUri)
            .withAspectRatio(ApplicationConstants.ASPECT_RATIO_FOR_CROPPING, ApplicationConstants.ASPECT_RATIO_FOR_CROPPING)
            .start(getActivity(), AddEditPatientFragment.this);
    }

    private String getUniqueImageFileName() {
        // Create an image file name
        @SuppressLint("SimpleDateFormat") String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        return timeStamp + "_" + ".jpg";
    }

    private Bitmap getResizedPortraitImage(String imagePath) {
        Bitmap portraitImg;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 4;
        Bitmap photo = BitmapFactory.decodeFile(output.getPath(), options);
        float rotateAngle;
        try {
            ExifInterface exifInterface = new ExifInterface(imagePath);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotateAngle = 270;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotateAngle = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotateAngle = 90;
                    break;
                default:
                    rotateAngle = 0;
                    break;
            }
            portraitImg = ImageUtils.rotateImage(photo, rotateAngle);
        } catch (IOException e) {
            logger.e(e.getMessage());
            portraitImg = photo;
        }
        return ImageUtils.resizePhoto(portraitImg);
    }

    @Override
    public void onCreateOptionsMenu(@NotNull Menu menu, @NotNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.submit_done_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.actionSubmit:
                submitAction();
                break;
            case R.id.actionReset:
                new AlertDialog.Builder(getActivity())
                    .setTitle(R.string.dialog_title_reset_patient)
                    .setMessage(R.string.reset_dialog_message)
                    .setPositiveButton(R.string.dialog_button_ok, (DialogInterface dialogInterface, int i) -> resetAction())
                    .setNegativeButton(R.string.dialog_button_cancel, null)
                    .show();
                break;
            default:
                // Do nothing
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void submitAction() {
        if (isUpdatePatient) {
            if (binding.deceasedCheckbox.isChecked() && !causeOfDeathUUID.isEmpty()) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext(),R.style.AlertDialogTheme);
                alertDialogBuilder.setTitle(R.string.mark_patient_deceased);
                // set dialog message
                alertDialogBuilder
                    .setMessage(R.string.mark_patient_deceased_notice)
                    .setCancelable(false)
                    .setPositiveButton(R.string.mark_patient_deceased_proceed, (dialog, id) -> {
                        dialog.cancel();
                        mPresenter.confirmUpdate(updatePatient(updatedPatient));
                    })
                    .setNegativeButton(R.string.dialog_button_cancel, (dialog, id) -> {
                        alertDialog.cancel();
                    });
                alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            } else {
                mPresenter.confirmUpdate(updatePatient(updatedPatient));
            }
        } else {
            mPresenter.confirmRegister(createPatient(), isPatientUnidentified);
        }
    }

    private void resetAction() {

        binding.firstName.setText("");
        binding.middlename.setText("");
        binding.surname.setText("");
        binding.dobEditText.setText("");
        binding.estimatedYear.setText("");
        binding.estimatedMonth.setText("");
        binding.addressOne.setText("");
        binding.addressTwo.setText("");
        binding.countryCodeSpinner.resetToDefaultCountry();
        binding.cityAutoComplete.setText("");
        binding.stateAutoComplete.setText("");
        binding.postalCode.setText("");
        binding.gender.clearCheck();

        binding.dobError.setText("");
        binding.gendererror.setText("");
        binding.addressError.setText("");

        binding.textInputLayoutFirstName.setError("");
        binding.textInputLayoutMiddlename.setError("");
        binding.textInputLayoutSurname.setError("");
        binding.textInputLayoutAddress.setError("");

        binding.patientPhoto.setImageResource(R.drawable.ic_person_grey_500_48dp);
        patientPhoto = null;
        patientName = null;
        isUpdatePatient = false;
        updatedPatient = null;
        output = null;
    }

    @Retention(SOURCE)
    @StringDef({StringValue.MALE, StringValue.FEMALE})
    public @interface StringValue {
        String FEMALE = "F";
        String MALE = "M";
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
