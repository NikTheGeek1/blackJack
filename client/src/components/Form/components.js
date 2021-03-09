import './FormInput.css';
import './FormButton.css';
import './FormSelect.css';

export const FormInput = ({ type, title, value, onChange, required, otherProps }) => {
    return (
        <>
            <label htmlFor={title}>{title}</label>
            <input
                {...otherProps}
                type={type ? type : "text"}
                id={title}
                name={title}
                onChange={onChange}
                value={value}
                required={required ? required : true}
            />
        </>
    );
};

export const FormButton = ({ title, otherProps }) => {
    return (
        <input type="submit" value={title} {...otherProps} />
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
