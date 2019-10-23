package org.sdfw.biometric.ui;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import org.sdfw.biometric.R;
import org.sdfw.biometric.databinding.ActivityHomeBinding;
import org.sdfw.biometric.databinding.NavHeaderHomeBinding;
import org.sdfw.biometric.delegate.EndDrawerToggle;
import org.sdfw.biometric.delegate.NavigationListener;
import org.sdfw.biometric.model.User;
import org.sdfw.biometric.viewmodel.HomeViewModel;

import javax.inject.Inject;

import static org.sdfw.biometric.util.Constant.TAG;

public class HomeActivity extends DaggerDialogActivity implements NavigationView.OnNavigationItemSelectedListener, NavigationListener {

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    private ActivityHomeBinding mBinding;
    private NavHeaderHomeBinding mHeaderBinding;
    private HomeViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_home);
        mHeaderBinding = DataBindingUtil.bind(mBinding.navView.getHeaderView(0));
        setupViewModel(savedInstanceState);
        observeFindUserStatus();
        setSupportActionBar(mBinding.appBarHome.toolbar);

        EndDrawerToggle toggle = new EndDrawerToggle(this, mBinding.drawerLayout,
                mBinding.appBarHome.toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mBinding.drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        mBinding.navView.setNavigationItemSelectedListener(this);

        Log.d(TAG, "onCreate: Home Activity " + this.hashCode());
        Fragment fragment;
        if (savedInstanceState == null) {
            // lauching for the first time
            fragment = DashboardFragment.newInstance();
        } else {
            // configuration change occurred
            fragment = getSupportFragmentManager().findFragmentById(mBinding.appBarHome.appBarContainer.container.getId());
        }

        if (!fragment.isInLayout()) {
            getSupportFragmentManager().beginTransaction()
                    .replace(mBinding.appBarHome.appBarContainer.container.getId(), fragment)
                    .commitNow();
        }
    }

    private void setupViewModel(Bundle savedInstanceState) {
        mViewModel = ViewModelProviders.of(this, viewModelFactory).get(HomeViewModel.class);
        if (savedInstanceState == null) {
            mViewModel.init();
        }
        mViewModel.findUser();
    }

    private void observeFindUserStatus() {
        mViewModel.getFindUserStatus().observe(this, user -> {
            if (user != null) {
                mHeaderBinding.setUser(user);
                configurePrivilege(user);
            }
        });
    }

    private int backPressCount = 0;

    @Override
    public void onBackPressed() {
        if (mBinding.drawerLayout.isDrawerOpen(GravityCompat.END)) {
            mBinding.drawerLayout.closeDrawer(GravityCompat.END);
        } else {
            Fragment fragment = getSupportFragmentManager().findFragmentById(mBinding.appBarHome.appBarContainer.container.getId());
            if (fragment instanceof DashboardFragment) {
                if (++backPressCount == 2) {
                    super.onBackPressed();
                } else {
                    Toast.makeText(this, R.string.press_back, Toast.LENGTH_SHORT).show();
                    new Handler().postDelayed(() -> backPressCount = 0, 2000);
                }
            } else {
                onNavigate(R.id.nav_home);
            }
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Fragment fragment = null;
        int title = 0;
        if (id == R.id.nav_home) {
            fragment = DashboardFragment.newInstance();
            title = R.string.title_activity_home;
        } else if (id == R.id.nav_memberships) {
            fragment = MemberMatrixFragment.newInstance();
            title = R.string.title_fragment_members;
        } else if (id == R.id.nav_loan_disbursements) {
            fragment = LoanDisbursementFragment.newInstance();
            title = R.string.title_fragment_loan_disbursements;
        } else if (id == R.id.nav_saving_withdrawns) {
            fragment = SavingWithdrawnFragment.newInstance();
            title = R.string.title_fragment_saving_withdrawns;
        } else if (id == R.id.nav_early_settlements) {
            fragment = EarlySettlementFragment.newInstance();
            title = R.string.title_fragment_early_settlements;
        } else if (id == R.id.nav_logout) {
            startActivity(new Intent(this, FingerprintAuthActivity.class));
            finish();
            return true;
        }

        getSupportFragmentManager().beginTransaction()
                .replace(mBinding.appBarHome.appBarContainer.container.getId(), fragment)
                .commitNow();
        getSupportActionBar().setTitle(title);

        mBinding.drawerLayout.closeDrawer(GravityCompat.END);
        return true;
    }

    @Override
    public void onNavigate(int id) {
        Fragment fragment = null;
        int title = 0;
        if (id == R.id.nav_home) {
            fragment = DashboardFragment.newInstance();
            title = R.string.title_activity_home;
        } else if (id == R.id.nav_memberships) {
            fragment = MemberMatrixFragment.newInstance();
            title = R.string.title_fragment_members;
        } else if (id == R.id.nav_loan_disbursements) {
            fragment = LoanDisbursementFragment.newInstance();
            title = R.string.title_fragment_loan_disbursements;
        } else if (id == R.id.nav_saving_withdrawns) {
            fragment = SavingWithdrawnFragment.newInstance();
            title = R.string.title_fragment_saving_withdrawns;
        } else if (id == R.id.nav_early_settlements) {
            fragment = EarlySettlementFragment.newInstance();
            title = R.string.title_fragment_early_settlements;
        }

        getSupportFragmentManager().beginTransaction()
                .replace(mBinding.appBarHome.appBarContainer.container.getId(), fragment)
                .commitNow();
        getSupportActionBar().setTitle(title);
        mBinding.navView.setCheckedItem(id);
    }

    private void configurePrivilege(User user) {
        Log.d(TAG, "configurePrivilege: " + user.getAppRole());
        if (user.getAppRole() == null) return;
        Menu menu = mBinding.navView.getMenu();
        switch (user.getAppRole()) {
            case "A":
                menu.findItem(R.id.nav_loan_disbursements).setEnabled(true);
                menu.findItem(R.id.nav_saving_withdrawns).setEnabled(true);
                menu.findItem(R.id.nav_early_settlements).setEnabled(true);
                break;
            case "B":
                menu.findItem(R.id.nav_memberships).setEnabled(true);
                break;
            case "C":
                menu.findItem(R.id.nav_memberships).setEnabled(true);
                menu.findItem(R.id.nav_loan_disbursements).setEnabled(true);
                menu.findItem(R.id.nav_saving_withdrawns).setEnabled(true);
                menu.findItem(R.id.nav_early_settlements).setEnabled(true);
                break;
        }
    }
}
