import './FormInput.css';
import './FormButton.css';
import './FormSelect.css';
import { useEffect, useState } from 'react';
import InfoMessage from '../InfoMessage/InfoMessage';

export const FormInput = ({ type, title, value, onChange, required, otherProps, infoMessage }) => {
    return (
        <div className="form-input-container">
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
            {infoMessage && <InfoMessage message={infoMessage} />}
        </div>
    );
};

export const FormButton = ({ title, otherProps, extraClasses }) => {
    return (
        <input type="submit" value={title} {...otherProps} className={`form-button ${extraClasses}`} />
    );
};

export const Button = ({ title, otherProps, extraClasses, onClick }) => {
    return (
        <button onClick={onClick} {...otherProps} className={`form-button ${extraClasses}`}>{title}</button>
    );
};


export const FormSelect = ({ otherProps, onChange, defaultValue, value, valuesArray, infoMessage }) => {
    const [activeClass, setActiveClass] = useState("");
    const [displayOptionsClass, setDisplayOptionsClass] = useState("");
    const [selectedOption, setSelectedOption] = useState(defaultValue);

    useEffect(() => {
        document.addEventListener("click", outterClickHandler);
        return () => {
            document.removeEventListener("click", outterClickHandler);
        }
    }, [activeClass]);

    const outterClickHandler = () => {
        if (activeClass) {
            setActiveClass("");
            setDisplayOptionsClass("");
        }
    };

    const toggleActiveHandler = () => {
        if (!activeClass) {
            setDisplayOptionsClass("select-options-display")
            setActiveClass("active");
        }
    };

    const clickOptionHandler = e => {
        onChange(e);
        setSelectedOption(e.target.getAttribute('rel'));
    };

    const valuesLI = valuesArray.map(v => <li onClick={e => clickOptionHandler(e)} className="select-li" key={v} rel={v}>{v}</li>);
    const valuesOptions = valuesArray.map(v => <option key={v} value={v}>{v}</option>);
    return (
        <div className="select">
            <select className="select-hidden" onChange={onChange} {...otherProps} value={value}>
                <option className="select-option" value="hide">-- {defaultValue} --</option>
                {valuesOptions}
            </select>
            <div className={"select-styled " + activeClass} onClick={toggleActiveHandler}>
                {selectedOption}
            </div>
            <ul className={"select-options " + displayOptionsClass}>
                <li className="select-li" rel="hide">-- {defaultValue} --</li>
                {valuesLI}
            </ul>
            {infoMessage && <InfoMessage message={infoMessage} />}
        </div>
    );
};

