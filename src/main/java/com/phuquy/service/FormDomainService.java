package com.phuquy.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


import com.phuquy.entity.Form;
import com.phuquy.entity.FormDomain;
import com.phuquy.entity.SkillDomain;
import com.phuquy.repository.FormDomainRepository;

import java.util.ArrayList;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FormDomainService {
    private final FormDomainRepository repository;

    public List<FormDomain> getList(){ return repository.findAll(); }
    public Boolean save(FormDomain form){
        repository.save(form);
        return true;
    }

    public List<SkillDomain> getListSkillDomainByFormId(Form form){
        List<FormDomain> list = repository.findByForm_FormID(form.getFormID());
        List<SkillDomain> listRe = new ArrayList<>();
        for (FormDomain formDomain : list) {
            listRe.add(formDomain.getDomain());
        }
        return listRe;
    }

}
