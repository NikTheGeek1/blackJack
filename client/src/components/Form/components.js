import './FormInput.css';
import './FormButton.css';
import './FormSelect.css';

export const FormInput = ({ type, title, value, onChange, required, otherProps }) => {
    return (
        <>
            {/* <label htmlFor={title}>{title}</label> */}
            <input className="form-input"
                {...otherProps}
                type={type ? type : "text"}
                id={title}
                name={title}
                placeholder={title}
                onChange={onChange}
                value={value}
                required={required ? required : true}
            />
        </>
    );
};

export const FormButton = ({ title, otherProps, extraClasses}) => {
    return (
        <input type="submit" value={title} {...otherProps} className={`form-button ${extraClasses}`} />
    );
};

export const Button = ({ title, otherProps, extraClasses, onClick}) => {
    return (
        <button onClick={onClick} {...otherProps} className={`form-button ${extraClasses}`}>{title}</button>
    );
};




export const FormSelect = ({ otherProps, onChange, children, defaultValue, value }) => {

    return (
        <select onChange={onChange} {...otherProps} value={value}>
            <option value="" disabled>{defaultValue}</option>
            {children}
        </select>
    );
};


export const FormOption = ({ title }) => {

    return (
            <option value={title}>{title}</option>
    );
};
