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
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.ExifInterface;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.hbb20.CountryCodePicker;

import org.jetbrains.annotations.NotNull;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.ACBaseFragment;
import org.openmrs.mobile.activities.dialog.CameraOrGalleryPickerDialog;
import org.openmrs.mobile.activities.dialog.CustomFragmentDialog;
import org.openmrs.mobile.activities.patientdashboard.PatientDashboardActivity;
import org.openmrs.mobile.activities.patientdashboard.details.PatientPhotoActivity;
import org.openmrs.mobile.application.OpenMRSLogger;
import org.openmrs.mobile.bundle.CustomDialogBundle;
import org.openmrs.mobile.listeners.watcher.PatientBirthdateValidatorWatcher;
import org.openmrs.mobile.models.Patient;
import org.openmrs.mobile.models.PersonAddress;
import org.openmrs.mobile.models.PersonName;
import org.openmrs.mobile.utilities.ApplicationConstants;
import org.openmrs.mobile.utilities.DateUtils;
import org.openmrs.mobile.utilities.ImageUtils;
import org.openmrs.mobile.utilities.StringUtils;
import org.openmrs.mobile.utilities.ToastUtil;
import org.openmrs.mobile.utilities.ViewUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.IOException;
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


@RuntimePermissions
public class AddEditPatientFragment extends ACBaseFragment<AddEditPatientContract.Presenter> implements AddEditPatientContract.View {

    private RelativeLayout relativeLayout;
    private LocalDate birthdate;
    private DateTime bdt;

    private TextInputLayout firstNameTIL;
    private TextInputLayout middleNameTIL;
    private TextInputLayout lastNameTIL;
    private TextInputLayout address1TIL;

    private EditText edfName;
    private EditText edmName;
    private EditText edlName;
    private EditText edDob;
    private EditText edYear;
    private EditText edMonth;
    private EditText edAddr1;
    private EditText edAddr2;
    private EditText edCity;
    private AutoCompleteTextView edState;
    private CountryCodePicker mCountryCodePicker;
    private EditText edPostal;

    private RadioGroup gen;
    private ProgressBar progressBar;

    private TextView fNameError;
    private TextView lNameError;
    private TextView dobError;
    private TextView genderError;
    private TextView addrError;

    private Button datePicker;

    private DateTimeFormatter dateTimeFormatter;

    private ImageView patientImageView;

    private FloatingActionButton capturePhotoBtn;
    private Bitmap patientPhoto = null;
    private String patientName;
    private File output = null;
    private final static int IMAGE_REQUEST = 1;
    private final static int GALLERY_IMAGE_REQUEST = 2;
    private OpenMRSLogger logger = new OpenMRSLogger();

    private boolean isUpdatePatient = false;
    private Patient updatedPatient;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_patient_info, container, false);
        setHasOptionsMenu(true);
        resolveViews(root);
        addListeners();
        fillFields(mPresenter.getPatientToUpdate());
        return root;
    }

    @Override
    public void finishPatientInfoActivity() {
        getActivity().finish();
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
            dobError.setVisibility(View.VISIBLE);

            dateTimeFormatter = DateTimeFormat.forPattern(DateUtils.DEFAULT_DATE_FORMAT);
            String minimumDate = DateTime.now().minusYears(
                    ApplicationConstants.RegisterPatientRequirements.MAX_PATIENT_AGE)
                    .toString(dateTimeFormatter);
            String maximumDate = DateTime.now().toString(dateTimeFormatter);

            dobError.setText(getString(R.string.dob_error, minimumDate, maximumDate));
        } else {
            dobError.setVisibility(View.GONE);
        }

        if (genderError) {
            this.genderError.setVisibility(View.VISIBLE);
        } else {
            this.genderError.setVisibility(View.GONE);
        }
    }

    @Override
    public void scrollToTop() {
        ScrollView scrollView = this.getActivity().findViewById(R.id.scrollView);
        scrollView.smoothScrollTo(0, scrollView.getPaddingTop());
    }


    private Patient updatePatientWithData(Patient patient) {
        String emptyError = getString(R.string.emptyerror);

        // Validate address
        if (ViewUtils.isEmpty(edAddr1)
                && ViewUtils.isEmpty(edAddr2)
                && ViewUtils.isEmpty(edCity)
                && ViewUtils.isEmpty(edPostal)
                && ViewUtils.isCountryCodePickerEmpty(mCountryCodePicker)
                && ViewUtils.isEmpty(edState)) {

            addrError.setText(R.string.atleastone);
            address1TIL.setErrorEnabled(true);
            address1TIL.setError(getString(R.string.atleastone));
        } else if (!ViewUtils.validateText(ViewUtils.getInput(edAddr1), ViewUtils.ILLEGAL_ADDRESS_CHARACTERS)
                || !ViewUtils.validateText(ViewUtils.getInput(edAddr2), ViewUtils.ILLEGAL_ADDRESS_CHARACTERS)) {

            addrError.setText(getString(R.string.addr_invalid_error));
            address1TIL.setErrorEnabled(true);
            address1TIL.setError(getString(R.string.addr_invalid_error));
        } else {
            address1TIL.setErrorEnabled(false);
        }

        // Add address
        PersonAddress address = new PersonAddress();
        address.setAddress1(ViewUtils.getInput(edAddr1));
        address.setAddress2(ViewUtils.getInput(edAddr2));
        address.setCityVillage(ViewUtils.getInput(edCity));
        address.setPostalCode(ViewUtils.getInput(edPostal));
        address.setCountry(mCountryCodePicker.getSelectedCountryName());
        address.setStateProvince(ViewUtils.getInput(edState));
        address.setPreferred(true);

        List<PersonAddress> addresses = new ArrayList<>();
        addresses.add(address);
        patient.setAddresses(addresses);

        // Validate names
        String givenNameEmpty = getString(R.string.fname_empty_error);
        // Invalid characters for given name only
        String givenNameError = getString(R.string.fname_invalid_error);
        // Invalid characters for the middle name
        String middleNameError = getString(R.string.midname_invalid_error);
        // Invalid family name
        String familyNameError = getString(R.string.lname_invalid_error);

        // First name validation
        if (ViewUtils.isEmpty(edfName)) {
            fNameError.setText(emptyError);
            firstNameTIL.setErrorEnabled(true);
            firstNameTIL.setError(emptyError);
        } else if (!ViewUtils.validateText(ViewUtils.getInput(edfName), ViewUtils.ILLEGAL_CHARACTERS)) {
            lNameError.setText(familyNameError);
            firstNameTIL.setErrorEnabled(true);
            firstNameTIL.setError(givenNameError);
        } else {
            firstNameTIL.setErrorEnabled(false);
        }

        // Middle name validation (can be empty)
        if (!ViewUtils.validateText(ViewUtils.getInput(edmName), ViewUtils.ILLEGAL_CHARACTERS)) {
            lNameError.setText(familyNameError);
            middleNameTIL.setErrorEnabled(true);
            middleNameTIL.setError(middleNameError);
        } else {
            middleNameTIL.setErrorEnabled(false);
        }

        // Family name validation
        if (ViewUtils.isEmpty(edlName)) {
            lNameError.setText(emptyError);
            lastNameTIL.setErrorEnabled(true);
            lastNameTIL.setError(emptyError);
        } else if (!ViewUtils.validateText(ViewUtils.getInput(edlName), ViewUtils.ILLEGAL_CHARACTERS)) {
            lNameError.setText(familyNameError);
            lastNameTIL.setErrorEnabled(true);
            lastNameTIL.setError(familyNameError);
        } else {
            lastNameTIL.setErrorEnabled(false);
        }

        // Add names
        PersonName name = new PersonName();
        name.setFamilyName(ViewUtils.getInput(edlName));
        name.setGivenName(ViewUtils.getInput(edfName));
        name.setMiddleName(ViewUtils.getInput(edmName));

        List<PersonName> names = new ArrayList<>();
        names.add(name);
        patient.setNames(names);

        // Add gender
        String[] genderChoices = {"M", "F"};
        int index = gen.indexOfChild(getActivity().findViewById(gen.getCheckedRadioButtonId()));
        if (index != -1) {
            patient.setGender(genderChoices[index]);
        } else {
            patient.setGender(null);
        }

        // Add birthdate
        String birthdate = null;
        if (ViewUtils.isEmpty(edDob)) {
            if (!StringUtils.isBlank(ViewUtils.getInput(edYear)) || !StringUtils.isBlank(ViewUtils.getInput(edMonth))) {
                dateTimeFormatter = DateTimeFormat.forPattern(DateUtils.OPEN_MRS_REQUEST_PATIENT_FORMAT);

                int yeardiff = ViewUtils.isEmpty(edYear) ? 0 : Integer.parseInt(edYear.getText().toString());
                int mondiff = ViewUtils.isEmpty(edMonth) ? 0 : Integer.parseInt(edMonth.getText().toString());
                LocalDate now = new LocalDate();
                bdt = now.toDateTimeAtStartOfDay().toDateTime();
                bdt = bdt.minusYears(yeardiff);
                bdt = bdt.minusMonths(mondiff);
                patient.setBirthdateEstimated(true);
                birthdate = dateTimeFormatter.print(bdt);
            }
        } else {
            String unvalidatedDate = edDob.getText().toString().trim();

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

        if (patientPhoto != null)
            patient.setPhoto(patientPhoto);

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
        progressBar.setVisibility(visibility ? View.VISIBLE : View.GONE);
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
        return (!ViewUtils.isEmpty(edfName) ||
                (!ViewUtils.isEmpty(edmName)) ||
                (!ViewUtils.isEmpty(edlName)) ||
                (!ViewUtils.isEmpty(edDob)) ||
                (!ViewUtils.isEmpty(edYear)) ||
                (!ViewUtils.isEmpty(edAddr1)) ||
                (!ViewUtils.isEmpty(edAddr2)) ||
                (!ViewUtils.isEmpty(edCity)) ||
                (!ViewUtils.isEmpty(edState)) ||
                (!ViewUtils.isCountryCodePickerEmpty(mCountryCodePicker)) ||
                (!ViewUtils.isEmpty(edPostal)));
    }

    public static AddEditPatientFragment newInstance() {
        return new AddEditPatientFragment();
    }

    private void resolveViews(View v) {
        relativeLayout = v.findViewById(R.id.addEditRelativeLayout);
        edfName = v.findViewById(R.id.firstname);
        edmName = v.findViewById(R.id.middlename);
        edlName = v.findViewById(R.id.surname);
        edDob = v.findViewById(R.id.dob);
        edYear = v.findViewById(R.id.estyr);
        edMonth = v.findViewById(R.id.estmonth);
        edAddr1 = v.findViewById(R.id.addr1);
        edAddr2 = v.findViewById(R.id.addr2);
        edCity = v.findViewById(R.id.city);
        edState = v.findViewById(R.id.state);
        mCountryCodePicker=v.findViewById(R.id.ccp);
        edPostal = v.findViewById(R.id.postal);

        gen = v.findViewById(R.id.gender);
        progressBar = v.findViewById(R.id.progress_bar);

        fNameError = v.findViewById(R.id.fnameerror);
        lNameError = v.findViewById(R.id.lnameerror);
        dobError = v.findViewById(R.id.doberror);
        genderError = v.findViewById(R.id.gendererror);
        addrError = v.findViewById(R.id.addrerror);

        datePicker = v.findViewById(R.id.btn_datepicker);
        capturePhotoBtn = v.findViewById(R.id.capture_photo);
        patientImageView = v.findViewById(R.id.patientPhoto);

        firstNameTIL = v.findViewById(R.id.textInputLayoutFirstName);
        middleNameTIL = v.findViewById(R.id.textInputLayoutMiddlename);
        lastNameTIL = v.findViewById(R.id.textInputLayoutSurname);
        address1TIL = v.findViewById(R.id.textInputLayoutAddress);
    }

    private void fillFields(final Patient patient) {
        if (patient != null) {
            //Change to Update Patient Form
            String updatePatientStr = getResources().getString(R.string.action_update_patient_data);
            this.getActivity().setTitle(updatePatientStr);

            isUpdatePatient = true;
            updatedPatient = patient;

            edfName.setText(patient.getName().getGivenName());
            edmName.setText(patient.getName().getMiddleName());
            edlName.setText(patient.getName().getFamilyName());

            patientName = patient.getName().getNameString();

            if (StringUtils.notNull(patient.getBirthdate()) || StringUtils.notEmpty(patient.getBirthdate())) {
                bdt = DateUtils.convertTimeString(patient.getBirthdate());
                edDob.setText(DateUtils.convertTime(DateUtils.convertTime(bdt.toString(), DateUtils.OPEN_MRS_REQUEST_FORMAT),
                        DateUtils.DEFAULT_DATE_FORMAT));
            }

            if (("M").equals(patient.getGender())) {
                gen.check(R.id.male);
            } else if (("F").equals(patient.getGender())) {
                gen.check(R.id.female);
            }

            edAddr1.setText(patient.getAddress().getAddress1());
            edAddr2.setText(patient.getAddress().getAddress2());
            edCity.setText(patient.getAddress().getCityVillage());
            edState.setText(patient.getAddress().getStateProvince());
            edPostal.setText(patient.getAddress().getPostalCode());

            if (patient.getPhoto() != null) {
                patientPhoto = patient.getPhoto();
                Bitmap resizedPatientPhoto = patient.getResizedPhoto();
                patientImageView.setImageBitmap(resizedPatientPhoto);
            }
        }
    }

    private void addSuggestionsToCities() {
        String country_name = mCountryCodePicker.getSelectedCountryName();
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
            edState.setAdapter(state_adapter);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        AddEditPatientFragmentPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    private void addListeners() {
        gen.setOnCheckedChangeListener((radioGroup, checkedId) -> genderError.setVisibility(View.GONE));
        edState.setOnFocusChangeListener((view, hasFocus) -> addSuggestionsToCities());

        edDob.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Only needs afterTextChanged method from TextWacher
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Only needs afterTextChanged method from TextWacher
            }

            @Override
            public void afterTextChanged(Editable s) {
                // If a considerable amount of text is filled in edDob, then remove 'Estimated age' fields.
                if (s.length() >= 8) {
                    edMonth.getText().clear();
                    edYear.getText().clear();
                }
            }
        });

        datePicker.setOnClickListener(v -> {
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

            edMonth.getText().clear();
            edYear.getText().clear();

            DatePickerDialog mDatePicker = new DatePickerDialog(AddEditPatientFragment.this.getActivity(), (datePicker, selectedYear, selectedMonth, selectedDay) -> {
                int adjustedMonth = selectedMonth + 1;
                edDob.setText(selectedDay + "/" + adjustedMonth + "/" + selectedYear);
                birthdate = new LocalDate(selectedYear, adjustedMonth, selectedDay);
                bdt = birthdate.toDateTimeAtStartOfDay().toDateTime();
            }, cYear, cMonth, cDay);
            mDatePicker.getDatePicker().setMaxDate(System.currentTimeMillis());
            mDatePicker.setTitle(getString(R.string.date_picker_title));
            mDatePicker.show();
        });

        capturePhotoBtn.setOnClickListener(view -> {

            CameraOrGalleryPickerDialog dialog = CameraOrGalleryPickerDialog.getInstance(
                    (dialog1, which) -> {
                                if (which == 0) {
                                    StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                                    StrictMode.setVmPolicy(builder.build());
                                    AddEditPatientFragmentPermissionsDispatcher.capturePhotoWithCheck(AddEditPatientFragment.this);
                                } else if (which == 1) {
                                    Intent i;
                                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT)
                                        i = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                                    else
                                        i = new Intent(Intent.ACTION_GET_CONTENT);
                                    i.addCategory(Intent.CATEGORY_OPENABLE);
                                    i.setType("image/*");
                                    startActivityForResult(i, GALLERY_IMAGE_REQUEST);
                                } else {
                                    patientImageView.setImageResource(R.drawable.ic_person_grey_500_48dp);
                                    patientImageView.invalidate();
                                    patientPhoto = BitmapFactory.decodeResource(getResources(), R.drawable.ic_person_grey_500_48dp);
                                }
                            }
                        );
                dialog.show(getChildFragmentManager(), null);
            }
        );


        patientImageView.setOnClickListener(view -> {
            if (output != null) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setDataAndType(Uri.fromFile(output), "image/jpeg");
                startActivity(i);
            } else if (patientPhoto != null) {
                Intent intent = new Intent(getContext(), PatientPhotoActivity.class);
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                patientPhoto.compress(Bitmap.CompressFormat.PNG, 0, byteArrayOutputStream);
                intent.putExtra("photo", byteArrayOutputStream.toByteArray());
                intent.putExtra("name", patientName);
                startActivity(intent);

            }
        });

        TextWatcher textWatcher = new PatientBirthdateValidatorWatcher(edDob, edMonth, edYear);
        edMonth.addTextChangedListener(textWatcher);
        edYear.addTextChangedListener(textWatcher);
    }

    @NeedsPermission({Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    public void capturePhoto() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getContext().getPackageManager()) != null) {
            File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
            output = new File(dir, getUniqueImageFileName());
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(output));
            startActivityForResult(takePictureIntent, IMAGE_REQUEST);
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
        Snackbar snackbar = Snackbar.make(relativeLayout, stringId, Snackbar.LENGTH_LONG);
        View sbView = snackbar.getView();
        TextView textView = sbView.findViewById(com.google.android.material.R.id.snackbar_text);
        textView.setTextColor(Color.WHITE);
        return snackbar;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == IMAGE_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                patientPhoto = getResizedPortraitImage(output.getPath());
                Bitmap bitmap = ThumbnailUtils.extractThumbnail(patientPhoto, patientImageView.getWidth(), patientImageView.getHeight());
                patientImageView.setImageBitmap(bitmap);
                patientImageView.invalidate();
            } else {
                output = null;
            }
        } else if (requestCode == GALLERY_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {

            try {
                ParcelFileDescriptor parcelFileDescriptor =
                        getActivity().getContentResolver().openFileDescriptor(data.getData(), "r");
                FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
                Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
                parcelFileDescriptor.close();

                patientPhoto = image;
                Bitmap bitmap = ThumbnailUtils.extractThumbnail(patientPhoto, patientImageView.getWidth(), patientImageView.getHeight());
                patientImageView.setImageBitmap(bitmap);
                patientImageView.invalidate();
            } catch (Exception e) {
                logger.e("Error getting image from gallery.", e);
            }
        }
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
                        .setTitle(R.string.dialog_title_are_you_sure)
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
            mPresenter.confirmUpdate(updatePatient(updatedPatient));
        } else {
            mPresenter.confirmRegister(createPatient());
        }
    }

    private void resetAction() {

        edfName.setText("");
        edmName.setText("");
        edlName.setText("");
        edDob.setText("");
        edYear.setText("");
        edMonth.setText("");
        edAddr1.setText("");
        edAddr2.setText("");
        mCountryCodePicker.resetToDefaultCountry();
        edCity.setText("");
        edState.setText("");
        edPostal.setText("");
        gen.clearCheck();

        fNameError.setText("");
        lNameError.setText("");
        dobError.setText("");
        genderError.setText("");
        addrError.setText("");

        firstNameTIL.setError("");
        middleNameTIL.setError("");
        lastNameTIL.setError("");
        address1TIL.setError("");

        patientImageView.setImageResource(R.drawable.ic_person_grey_500_48dp);
        patientPhoto = null;
        patientName = null;
        isUpdatePatient = false;
        updatedPatient = null;
        output = null;
    }
}
