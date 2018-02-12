package br.org.meiaentrada.validadorcie.model;

import br.org.meiaentrada.validadorcie.presenter.ValidationPresenter;
import br.org.meiaentrada.validadorcie.view.ValidationView;


public class ValidationPresenterImpl implements ValidationPresenter {

    private ValidationView validationView;

    public ValidationPresenterImpl(ValidationView validationView) {

        this.validationView = validationView;

    }

}
